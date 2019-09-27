package ru.sigmadigital.pantus.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.dialogs.WaitDialog;
import ru.sigmadigital.pantus.fragments.ARFragment;
import ru.sigmadigital.pantus.fragments.CategoriesFragment;
import ru.sigmadigital.pantus.fragments.ContactsFragment;
import ru.sigmadigital.pantus.fragments.NewsItemFragment;
import ru.sigmadigital.pantus.fragments.NewsListFragment;
import ru.sigmadigital.pantus.fragments.OrderFormFragment;
import ru.sigmadigital.pantus.fragments.ProductFragment;
import ru.sigmadigital.pantus.fragments.ProductsListFragment;
import ru.sigmadigital.pantus.models.ItemResponse;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    //action bar
    private LinearLayout actionBar;
    private ImageView btBack;
    private ImageView btPhone;
    private ImageView btLocate;
    private ImageView btSearch;
    private EditText etSearch;
    private ImageView btClose;
    private OnGetSearchTextListener searchListener;

    //bottom navigation
    private LinearLayout bottomNav;
    private View vProductsAct;
    private View vNewsAct;
    private LinearLayout productsButton;
    private LinearLayout newsButton;
    private ImageView ARButton;
    private WaitDialog waitDialog;

    private static MainActivity instance = null;

    public static MainActivity getInstance(){
        return MainActivity.instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.instance = this;
        setContentView(R.layout.activity_main);
        waitDialog = new WaitDialog(this);
        initViews();
        if (savedInstanceState == null) {
            loadFragment(ARFragment.newInstance(), "", true);
            //loadFragment(OrderFormFragment.newInstance(new ItemResponse()), "", true);
        }
        setMenuAct();


        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setMenuAct();
            }
        });
    }

    public void showWait(int title){
        waitDialog.show(title);
    }

    public void hideWait(){
        waitDialog.hide();
    }

    private void initViews() {

        //action bar
        actionBar = findViewById(R.id.action_bar);
        btBack = findViewById(R.id.back);
        btPhone = findViewById(R.id.phone);
        btLocate = findViewById(R.id.locate);
        btSearch = findViewById(R.id.search);
        etSearch = findViewById(R.id.et_search);
        btClose = findViewById(R.id.close);
        btBack.setOnClickListener(this);
        btPhone.setOnClickListener(this);
        btLocate.setOnClickListener(this);
        btSearch.setOnClickListener(this);
        btClose.setOnClickListener(this);
        setChangeListener();

        //bottom navigation
        bottomNav = findViewById(R.id.ll_bottom);
        vProductsAct = findViewById(R.id.v_products_act);
        vNewsAct = findViewById(R.id.v_news_act);
        productsButton = findViewById(R.id.ll_products);
        newsButton = findViewById(R.id.ll_news);
        ARButton = findViewById(R.id.ar_button);
        productsButton.setOnClickListener(this);
        newsButton.setOnClickListener(this);
        ARButton.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.instance = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.instance = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //action bar
            case R.id.back:
                onBackPressed();
                break;

            case R.id.phone:
                call();
                break;

            case R.id.locate:
                setMenuNotAct();
                loadFragment(ContactsFragment.newInstance(), "", true);
                break;

            case R.id.search:
                if (etSearch.getVisibility() == View.VISIBLE) {
                    etSearch.setText("");
                    showSearchEditText(false);
                } else {
                    showSearchEditText(true);
                }
                break;

            case R.id.close:
                onBackPressed();
                break;


            //bottom nav
            case R.id.ll_products:
                setProductsAct();
                loadFragment(CategoriesFragment.newInstance(), "", true);
                break;

            case R.id.ll_news:
                setNewsAct();
                loadFragment(NewsListFragment.newInstance(), "", true);
                break;

            case R.id.ar_button:
                setMenuNotAct();
                loadFragment(ARFragment.newInstance(), "", true);
                break;
        }
    }


    //action bar
    public void showActionBar(boolean show) {
        if (show) {
            actionBar.setVisibility(View.VISIBLE);
        } else {
            actionBar.setVisibility(View.GONE);
        }
    }

    public void showBackButton(boolean show) {
        if (show) {
            btBack.setVisibility(View.VISIBLE);
        } else {
            btBack.setVisibility(View.GONE);
        }
    }

    public void showCloseButton(boolean show) {
        if (show) {
            btClose.setVisibility(View.VISIBLE);
        } else {
            btClose.setVisibility(View.GONE);
        }
    }

    public void showSearchButton(boolean show) {
        if (show) {
            btSearch.setVisibility(View.VISIBLE);
        } else {
            btSearch.setVisibility(View.GONE);
            showSearchEditText(false);
        }
    }

    public void showSearchEditText(boolean show) {
        if (show) {
            etSearch.setVisibility(View.VISIBLE);
            btSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        } else {
            etSearch.setVisibility(View.GONE);
            /*etSearch.setText("");*/
            btSearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
            hideKeyboard(getCurrentFocus());
        }
    }

    private void setChangeListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchListener != null)
                    searchListener.onGetSearchedText(getSearchText());
            }
        });
    }

    public void setSearchChangeListener(OnGetSearchTextListener listener) {
        searchListener = listener;
    }

    public void removeSearchChangeListener() {
        searchListener = null;
    }

    public String getSearchText() {
        return etSearch.getText().toString();
    }

    public boolean isSearchEditTextVisible() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }


    //bottom navigation
    public void showBottomNav(boolean show) {
        if (show) {
            bottomNav.setVisibility(View.VISIBLE);
            ARButton.setVisibility(View.VISIBLE);
        } else {
            bottomNav.setVisibility(View.GONE);
            ARButton.setVisibility(View.GONE);
        }
    }

    private void setMenuAct() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1);

                if (isNewsAct(fragment)) {
                    setNewsAct();
                    return;
                }
                if (isProductsAct(fragment)) {
                    setProductsAct();
                    return;
                }
                setMenuNotAct();
            } else {
                setMenuNotAct();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMenuNotAct() {
        vProductsAct.setVisibility(View.INVISIBLE);
        vNewsAct.setVisibility(View.INVISIBLE);
    }

    private void setProductsAct() {
        vProductsAct.setVisibility(View.VISIBLE);
        vNewsAct.setVisibility(View.INVISIBLE);
    }

    private void setNewsAct() {
        vProductsAct.setVisibility(View.INVISIBLE);
        vNewsAct.setVisibility(View.VISIBLE);
    }

    private boolean isNewsAct(Fragment fragment) {
        return fragment.getClass().equals(NewsListFragment.class) || fragment.getClass().equals(NewsItemFragment.class);
    }

    private boolean isProductsAct(Fragment fragment) {
        return fragment.getClass().equals(CategoriesFragment.class) || fragment.getClass().equals(ProductsListFragment.class) || fragment.getClass().equals(ProductFragment.class);
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.phone)));
        startActivity(intent);
    }

    public void enableOnlyPortraitOrientation(boolean bool) {
        if (bool) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("restart", true);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }

    public interface OnGetSearchTextListener {
        void onGetSearchedText(String query);
    }

}


