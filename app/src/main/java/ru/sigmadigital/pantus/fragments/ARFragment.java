package ru.sigmadigital.pantus.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.easyar.Engine;
import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.activities.PlayerActivity;
import ru.sigmadigital.pantus.adapters.ModelsAdapter;
import ru.sigmadigital.pantus.adapters.TextButtonsAdapter;
import ru.sigmadigital.pantus.api.asynctask.ItemTask;
import ru.sigmadigital.pantus.ar.GLView;
import ru.sigmadigital.pantus.dialogs.ModelSelectDialog;
import ru.sigmadigital.pantus.models.ItemResponse;
import ru.sigmadigital.pantus.models.ItemTextResponse;
import ru.sigmadigital.pantus.util.SettingsHelper;

public class ARFragment extends BaseFragment implements View.OnClickListener, OnModelDetectListener, TextButtonsAdapter.OnTextClickListener, ModelsAdapter.OnModelClickListener {
    private static final String API_KEY = "oImAYqMPIOABCInofKYXJS7HhkJhj6Q7uOJgikZOsZbeIm0yCnSMO5fX8BqNxJ8Os4vHCHsCWeYqVbCve8vh8UqpqXuzVmfnqavk8oG548208xV8YOEYwpJxVoSGNmfw8f0GGUOxvIPSdX086fMNWVYBIhrYFSSQzHoyhgCvgnMGQyqnJTaVFum1nw0IwstXy9NiSYtG";

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private boolean hasPermission = false;

    private GLView glView;

    private RecyclerView rvTexts, rvModels;
    private TextView tvText;
    private CardView cvText;
    private TextButtonsAdapter textButtonsAdapter;
    private ModelsAdapter modelsAdapter;

    private int curText = -1;

    private ImageView btVideo;
    private ImageView btChain;
    private ImageView scan;
    private FrameLayout container;
    private TextView permissionDenied;
    private TextView permissionSettings;

    private String curItemName = null;

    private ItemResponse item;

    private static final int GL_VIEW_ID = 106;


    public static ARFragment newInstance() {
        return new ARFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ar, container, false);

        try {
            if (!Engine.initialize(getActivity(), API_KEY)) {
                Log.e("HelloAR", "Initialization Failed.");
            } else {
                Log.e("HelloAR", "Initialization Done.");
            }
        } catch (Exception e) {
            Log.e("App", "er", e);
        }

        scan = v.findViewById(R.id.scan);
        /*Drawable d = getResources().getDrawable(R.drawable.ic_scan);
        d.setColorFilter(getResources().getColor(R.color.white_transparent), PorterDuff.Mode.SRC_IN);
        scan.setImageDrawable(d);*/

        if (SettingsHelper.isFirstRun()) {
            showDialog();
            SettingsHelper.setFirstRun();
        }

        textButtonsAdapter = new TextButtonsAdapter(new ArrayList<ItemTextResponse>(), getActivity(), this);
        modelsAdapter = new ModelsAdapter(getActivity(), this);

        rvModels = v.findViewById(R.id.rv_models);
        rvTexts = v.findViewById(R.id.rv_strings_help);
        cvText = v.findViewById(R.id.cv_text);
        tvText = v.findViewById(R.id.tv_text);

        rvTexts.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rvTexts.setAdapter(textButtonsAdapter);

        rvModels.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvModels.setAdapter(modelsAdapter);

        this.container = v.findViewById(R.id.container);
        permissionDenied = v.findViewById(R.id.permission_denied);
        permissionDenied.setOnClickListener(this);
        permissionSettings = v.findViewById(R.id.settings);
        permissionSettings.setOnClickListener(this);
        btVideo = v.findViewById(R.id.bt_video);
        btVideo.setOnClickListener(this);
        btChain = v.findViewById(R.id.bt_chain);
        btChain.setOnClickListener(this);
        cvText.setOnClickListener(this);

        glView = new GLView(App.getAppContext());
        glView.setId(GL_VIEW_ID);
        glView.setOnClickListener(this);

        getPermissions();
        if (savedInstanceState != null && savedInstanceState.containsKey("item")) {
            item = (ItemResponse) savedInstanceState.getSerializable("item");
        }


        glView.setDetectListener(this);
        rvTexts.setVisibility(View.VISIBLE);
        showButtons();
        return v;
    }


    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        } else {
            addGlViewToContainer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE && grantResults.length != 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGlViewToContainer();
            } else {
                showPermissionDenied(true);
            }
        }
    }

    private void addGlViewToContainer() {
        if (glView != null && container.getChildCount() == 0) {
            container.addView(glView, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            showPermissionDenied(false);
        }
    }

    @Override
    public void onModelDetect(String name) {
        long id = -1;
        try {
            id = Long.parseLong(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showButtons();
        if (id > 0) {
            new ItemTask(id, new ItemTask.OnGetItemListener() {
                @Override
                public void onGetItem(ItemResponse itemResponse) {
                    curItemName = null;
                    item = itemResponse;
                    showButtons();
                    if (item.getItemTypes() != null && item.getItemTypes().contains(";")) {
                        String[] types = item.getItemTypes().split(Pattern.quote(";"));
                        new ModelSelectDialog(getActivity(), new ModelSelectDialog.OnModelSelectListener() {
                            @Override
                            public void onModelSelect(String name) {
                                curItemName = name;
                                int subPos = getSubPos();
                                onGetModelsCount();
                                glView.loadModel(0, subPos);
                            }
                        }, types);
                        Log.e("App", new Gson().toJson(types));

                    } else {
                        onGetModelsCount();
                        glView.loadModel(0, -1);
                    }
                }
            }).execute();
        }
    }

    private int getSubPos() {
        String[] types = item.getItemTypes().split(Pattern.quote(";"));
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(curItemName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onGetModelsCount() {
        int count = getModelsCount();
        if (count == 1) {
            modelsAdapter.setCount(0);
        } else {
            modelsAdapter.setCount(count);
        }
    }

    private int getModelsCount() {
        int count = 0;
        try {
            if (curItemName == null) {
                String[] names = new File(App.getFilesPath() + "/models/" + item.getId()).list();
                for (String nm : names) {
                    if (nm.toLowerCase().endsWith(".obj")) {
                        count++;
                    }
                }
            } else {
                String[] subPosNames = new File(App.getFilesPath() + "/models/" + item.getId()).list();
                String[] names = new File(App.getFilesPath() + "/models/" + item.getId() + "/" + subPosNames[getSubPos()]).list();
                int i = 0;
                String name = "";
                for (String nm : names) {
                    if (nm.toLowerCase().endsWith(".obj")) {
                        count++;
                    }
                }
            }
        } catch (Exception e) {
        }
        return count;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            addGlViewToContainer();
        }

        if (glView != null) {
            glView.onResume();
        }

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showSearchButton(false);
            ((MainActivity) getActivity()).enableOnlyPortraitOrientation(true);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (item != null) {
            outState.putSerializable("item", item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).enableOnlyPortraitOrientation(false);
        if (glView != null)
            glView.onPause();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_video:
                Intent intent = new Intent(App.getAppContext(), PlayerActivity.class);
                intent.putExtra("video_link", item.getVideo() != null ? item.getVideo() : "");
                startActivity(intent);
                break;

            case R.id.bt_chain:
                loadFragment(WebViewFragment.newInstance(item.getLink() != null ? item.getLink() : ""), "", true);
                break;

            case R.id.permission_denied:
                goToSettings();
                break;

            case R.id.settings:
                goToSettings();
                break;
            case GL_VIEW_ID:
                textButtonsAdapter.disableSelected();
                cvText.setVisibility(View.GONE);
                curText = -1;
                break;
            case R.id.cv_text:
                cvText.setVisibility(View.GONE);
                break;
        }
    }

    private void showDialog() {
        DialogInfo dialogFragment = DialogInfo.newInstance();
        dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);

        if (getFragmentManager() != null)
            dialogFragment.show(getFragmentManager(), "DialogCounter");
    }

    private void showButtons() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (item != null) {

                        textButtonsAdapter.setElements(item.getTexts());
                        if (item.getVideo() != null && item.getVideo().length() > 0) {
                            btVideo.setVisibility(View.VISIBLE);
                        } else {
                            btVideo.setVisibility(View.GONE);
                        }
                        if (item.getLink() != null && item.getLink().length() > 0) {
                            btChain.setVisibility(View.VISIBLE);
                        } else {
                            btChain.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        curText = -1;
                        btVideo.setVisibility(View.GONE);
                        textButtonsAdapter.setElements(new ArrayList<ItemTextResponse>());
                        cvText.setVisibility(View.INVISIBLE);
                        btChain.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    private void showPermissionDenied(boolean show) {
        if (show) {
            permissionDenied.setVisibility(View.VISIBLE);
            permissionSettings.setVisibility(View.VISIBLE);
            scan.setVisibility(View.GONE);
        } else {
            permissionDenied.setVisibility(View.GONE);
            permissionSettings.setVisibility(View.GONE);
            scan.setVisibility(View.VISIBLE);
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + App.getAppContext().getPackageName()));
        startActivity(intent);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }


    @Override
    public void onTextCkick(String text, int pos) {
        if (curText == pos) {
            curText = -1;
            cvText.setVisibility(View.GONE);
        } else {
            cvText.setVisibility(View.VISIBLE);
            tvText.setText(text);
            curText = pos;
        }


    }

    @Override
    public void onModelClick(int pos) {
        glView.loadModel(pos, getSubPos());
    }
}
