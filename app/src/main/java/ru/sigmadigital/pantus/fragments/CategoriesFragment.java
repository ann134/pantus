package ru.sigmadigital.pantus.fragments;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.adapters.CategoriesAdapter;
import ru.sigmadigital.pantus.api.asynctask.CategoriesTask;
import ru.sigmadigital.pantus.models.CategoryArrayResponse;
import ru.sigmadigital.pantus.models.CategoryResponse;


public class CategoriesFragment extends BaseFragment implements CategoriesAdapter.OnCategoryClickListener, MainActivity.OnGetSearchTextListener {

    private Bundle rvState;

    private CategoriesTask categoriesTask;

    private RecyclerView recyclerView;
    private CategoriesAdapter adapter;
    private ProgressBar loading;
    private TextView textError;

    private boolean loadProducts = false;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_categories, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ConstraintLayout root = v.findViewById(R.id.root);
        loading = v.findViewById(R.id.loading);
        textError = v.findViewById(R.id.textError);
        recyclerView = v.findViewById(R.id.recycler_view);
        adapter = new CategoriesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }


        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else if (rvState != null) {
            restoreFromBundle(rvState);
        } else {
            getCategories();
        }

        return v;
    }

    private void getCategories() {
        categoriesTask = new CategoriesTask(new CategoriesTask.OnGetCategoriesResponseListener() {
            @Override
            public void onGetCategoriesResponse(CategoryArrayResponse categoryArrayResponse) {
                if (categoryArrayResponse != null) {
                    setData(categoryArrayResponse);
                } else {
                    error();
                }
            }
        });
        categoriesTask.execute();
    }

    private void setData(CategoryArrayResponse categories) {
        adapter.setData(categories);
        adapter.notifyDataSetChanged();
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.GONE);
    }

    private void error() {
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetSearchedText(String query) {
        if (!query.equals("")) {
            loadProducts = true;
            loadFragment(ProductsListFragment.newInstance(query), "", true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showSearchButton(true);
        }

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setSearchChangeListener(this);
        }

        ((MainActivity) getActivity()).showSearchEditText(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).removeSearchChangeListener();
            if (!loadProducts) {
                ((MainActivity) getActivity()).hideKeyboard(getView());
            }
        }
        if (categoriesTask != null)
            categoriesTask.cancel(true);

        rvState = new Bundle();
        rvState.putSerializable("categories", new CategoryArrayResponse(adapter.getCategoryList()));
        rvState.putParcelable("position", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("categories", new CategoryArrayResponse(adapter.getCategoryList()));
        outState.putParcelable("position", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void restoreFromBundle(Bundle bundle) {
        if (bundle.containsKey("categories") && bundle.containsKey("position")) {
            CategoryArrayResponse categoties = ((CategoryArrayResponse) bundle.getSerializable("categories"));
            if (categoties != null && categoties.getCategories().size() != 0) {
                setData(categoties);
            } else {
                getCategories();
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(bundle.getParcelable("position"));
        }
    }

    @Override
    public void onCategoryClick(CategoryResponse categoryResponse) {
        loadProducts = true;
        loadFragment(ProductsListFragment.newInstance(categoryResponse), "", true);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }


}
