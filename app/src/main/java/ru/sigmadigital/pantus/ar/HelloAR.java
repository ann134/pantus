package ru.sigmadigital.pantus.ar;


import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLES20;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.easyar.CameraCalibration;
import cn.easyar.CameraDevice;
import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDeviceType;
import cn.easyar.CameraFrameStreamer;
import cn.easyar.Frame;
import cn.easyar.FunctorOfVoidFromPointerOfTargetAndBool;
import cn.easyar.ImageTarget;
import cn.easyar.ImageTracker;
import cn.easyar.Renderer;
import cn.easyar.StorageType;
import cn.easyar.Target;
import cn.easyar.TargetInstance;
import cn.easyar.TargetStatus;
import cn.easyar.Vec2I;
import cn.easyar.Vec4I;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.api.asynctask.GetModelTask;
import ru.sigmadigital.pantus.api.asynctask.LoadModelTask;
import ru.sigmadigital.pantus.ar.rendering.ConfigCreator;
import ru.sigmadigital.pantus.ar.rendering.ModelRenderer;
import ru.sigmadigital.pantus.ar.rendering.RenderModel;
import ru.sigmadigital.pantus.dialogs.WaitDialog;
import ru.sigmadigital.pantus.fragments.DialogInfo;
import ru.sigmadigital.pantus.fragments.OnModelDetectListener;

public class HelloAR implements GetModelTask.OnGetModelListener, LoadModelTask.OnLoadModelListener {
    private CameraDevice camera;
    private CameraFrameStreamer streamer;
    private ArrayList<ImageTracker> trackers;
    private Renderer videobg_renderer;
    private ModelRenderer box_renderer;
    private boolean viewport_changed = false;
    private Vec2I view_size = new Vec2I(0, 0);
    private int rotation = 0;
    private Vec4I viewport = new Vec4I(0, 0, 1280, 720);
    private Context context;
    private GetModelTask modelLoader;
    private WaitDialog waitDialog;
    private OnModelDetectListener modelDetectListener;
    private LoadModelTask loadModelTask;
    private EGLConfig eglConfig;

    private String curModel = "";
    private RenderModel oModel = null;

    public HelloAR(Context context, OnModelDetectListener detectListener) {
        trackers = new ArrayList<>();
        this.context = context;
        this.modelDetectListener = detectListener;
        waitDialog = new WaitDialog(MainActivity.getInstance());
    }

    public void setModelDetectListener(OnModelDetectListener modelDetectListener) {
        this.modelDetectListener = modelDetectListener;
    }


    private void loadFromJsonString(ImageTracker tracker) {

        String json = ConfigCreator.create(context);
        Log.e("App", json);
        List<ImageTarget> targets = ImageTarget.setupAll(json, StorageType.App | StorageType.Json);
        for (ImageTarget target : targets) {

            tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
                @Override
                public void invoke(Target target, boolean status) {
                    Log.e("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
                }
            });
        }

    }

    public void setEglConfig(EGLConfig eglConfig) {
        this.eglConfig = eglConfig;
    }

    public EGLConfig getEglConfig() {
        return eglConfig;
    }

    public boolean initialize() {
        camera = new CameraDevice();
        streamer = new CameraFrameStreamer();
        streamer.attachCamera(camera);

        boolean status = true;
        status &= camera.open(CameraDeviceType.Default);
        camera.setSize(new Vec2I(1280, 720));

        if (!status) {
            return status;
        }
        ImageTracker tracker = new ImageTracker();
        tracker.attachStreamer(streamer);
        loadFromJsonString(tracker);
        trackers.add(tracker);

        return status;
    }

    public void dispose() {
        for (ImageTracker tracker : trackers) {
            tracker.dispose();
        }
        trackers.clear();
        box_renderer = null;
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
            videobg_renderer = null;
        }
        if (streamer != null) {
            streamer.dispose();
            streamer = null;
        }
        if (camera != null) {
            camera.dispose();
            camera = null;
        }
    }

    public boolean start() {
        boolean status = true;
        status &= (camera != null) && camera.start();
        status &= (streamer != null) && streamer.start();
        camera.setFocusMode(CameraDeviceFocusMode.Continousauto);
        for (ImageTracker tracker : trackers) {
            status &= tracker.start();
        }
        return status;
    }

    public boolean stop() {
        boolean status = true;
        for (ImageTracker tracker : trackers) {
            status &= tracker.stop();
        }
        status &= (streamer != null) && streamer.stop();
        status &= (camera != null) && camera.stop();
        return status;
    }

    /**
     * onSurfaceCreated
     */
    public void initGL(RenderModel renderModel, GL10 gl, EGLConfig config) {
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
        }
        videobg_renderer = new Renderer();
        box_renderer = new ModelRenderer(context, renderModel);
        box_renderer.onSurfaceCreated(gl, config);

    }

    public void initGL(GL10 gl, EGLConfig config) {
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
        }
        videobg_renderer = new Renderer();
        if (box_renderer != null) {
            box_renderer.onSurfaceCreated(gl, config);
        }

    }

    /**
     * onSurfaceChanged
     *
     * @param width
     * @param height
     */
    public void resizeGL(GL10 gl, int width, int height) {
        if (box_renderer != null) {
            box_renderer.onSurfaceChanged(gl, width, height);
        }
        view_size = new Vec2I(width, height);
        viewport_changed = true;
    }

    private void updateViewport() {
        CameraCalibration calib = camera != null ? camera.cameraCalibration() : null;
        int rotation = calib != null ? calib.rotation() : 0;
        if (rotation != this.rotation) {
            this.rotation = rotation;
            viewport_changed = true;
        }
        if (viewport_changed) {
            Vec2I size = new Vec2I(1, 1);
            if ((camera != null) && camera.isOpened()) {
                size = camera.size();
            }
            if (rotation == 90 || rotation == 270) {
                size = new Vec2I(size.data[1], size.data[0]);
            }

            float scaleRatio = Math.max((float) view_size.data[0] / (float) size.data[0], (float) view_size.data[1] / (float) size.data[1]);
            Vec2I viewport_size = new Vec2I(Math.round(size.data[0] * scaleRatio), Math.round(size.data[1] * scaleRatio));
            viewport = new Vec4I((view_size.data[0] - viewport_size.data[0]) / 2, (view_size.data[1] - viewport_size.data[1]) / 2, viewport_size.data[0], viewport_size.data[1]);

            if ((camera != null) && camera.isOpened())
                viewport_changed = false;
        }
    }


    public void render(GL10 gl) {

//        box_renderer.onDrawFrame(gl);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (videobg_renderer != null) {
            Vec4I default_viewport = new Vec4I(0, 0, view_size.data[0], view_size.data[1]);
            GLES20.glViewport(default_viewport.data[0], default_viewport.data[1], default_viewport.data[2], default_viewport.data[3]);
            if (videobg_renderer.renderErrorMessage(default_viewport)) {
                return;
            }
        }

        if (streamer == null) {
            return;
        }
        Frame frame = streamer.peek();
        try {
            updateViewport();

            GLES20.glViewport(viewport.data[0], viewport.data[1], viewport.data[2], viewport.data[3]);

            if (videobg_renderer != null) {
                videobg_renderer.render(frame, viewport);
            }

            for (TargetInstance targetInstance : frame.targetInstances()) {
                int status = targetInstance.status();
                if (status == TargetStatus.Tracked) {
                    Target target = targetInstance.target();
                    ImageTarget imagetarget = target instanceof ImageTarget ? (ImageTarget) (target) : null;
                    if (imagetarget == null) {
                        continue;
                    }
                    String model = imagetarget.name();

                    // Log.e("App", "Flame=" + frame.index() + " " + frame.text() + " " + imagetarget.name());

                    if (oModel != null) {
                        box_renderer = new ModelRenderer(context, oModel);
                        box_renderer.onSurfaceCreated(gl, getEglConfig());
                        oModel = null;

                    }
                    if (!curModel.equals(model)) {
                        try {
                            if (modelDetectListener != null) {
                                modelDetectListener.onModelDetect(model);
                            }
                            if (modelLoader != null && !modelLoader.getId().equals(model)) {
                                modelLoader.cancel(true);
                            }
                            modelLoader = new GetModelTask(context, model, HelloAR.this);
                            modelLoader.execute();
                            curModel = model;
                            if (MainActivity.getInstance() != null) {
                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        waitDialog.show(R.string.load_model);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.e("App", "er", e);
                        }
                    }

                    if (box_renderer != null) {
                        box_renderer.onDrawFrame(gl, camera.projectionGL(0.2f, 500.f), targetInstance.poseGL(), imagetarget.size());
                    }

                }
            }
        } finally {
            frame.dispose();
        }
    }

    /**
     * onDrawFrame
     */


    @Override
    public void onGetModel() {
        modelLoader = null;
        if (modelDetectListener != null) {
            modelDetectListener.onGetModelsCount();
        }
        /*подсветить на экране модели*/
       /* if (count > 0) {
            loadModel(0);
        }*/
    }

    public void loadModel(int pos, int subpos) {
        loadModelTask = new LoadModelTask(curModel, pos, subpos, this);
        loadModelTask.execute();
        waitDialog.show(R.string.load_model);
    }

    @Override
    public void onNeedLoadModelFromNetwork() {
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waitDialog.hide();
                    waitDialog.show(R.string.load_model_inet);
                }
            });
        }

    }

    @Override
    public void onLoadModel(RenderModel model) {
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waitDialog.hide();
                }
            });
        }
        if (model != null) {
            oModel = model;
            box_renderer = new ModelRenderer(context, model);
        }
        if (model == null) {
            Log.e("App", "Model is null");
            if (MainActivity.getInstance() != null) {

                DialogInfo dialogFragment = DialogInfo.newInstance(context.getString(R.string.error), context.getString(R.string.error_load_model), false);
                dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);

                if (MainActivity.getInstance().getSupportFragmentManager() != null) {
                    dialogFragment.show(MainActivity.getInstance().getSupportFragmentManager(), "DialogCounter");
                }
                dialogFragment.setDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        curModel = "";
                    }
                });

            }
            Log.e("App", "Model is null1");
        }
    }
}