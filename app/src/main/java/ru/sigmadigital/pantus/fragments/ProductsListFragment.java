package ru.sigmadigital.pantus.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.adapters.ProductsListAdapter;
import ru.sigmadigital.pantus.api.asynctask.ItemsTask;
import ru.sigmadigital.pantus.api.asynctask.SearchTask;
import ru.sigmadigital.pantus.models.CategoryResponse;
import ru.sigmadigital.pantus.models.ItemArrayResponse;
import ru.sigmadigital.pantus.models.ItemResponse;


public class ProductsListFragment extends BaseFragment implements ProductsListAdapter.OnButtonBuyListener, MainActivity.OnGetSearchTextListener, ProductsListAdapter.LoadMoreListener {

    private int count = 10;
    private Bundle rvState;

    private CategoryResponse currCategory;
    private String query;

    private ItemsTask itemsTask;
    private SearchTask searchTask;
    private ItemArrayResponse itemArrayResponse;

    private RecyclerView recyclerView;
    private ProductsListAdapter adapter;
    private ProgressBar loading;
    private TextView textError;
    private TextView textNothing;


    public static ProductsListFragment newInstance(CategoryResponse categoryResponse) {
        ProductsListFragment fragment = new ProductsListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", categoryResponse);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ProductsListFragment newInstance(String query) {
        ProductsListFragment fragment = new ProductsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("category")) {
            currCategory = (CategoryResponse) getArguments().getSerializable("category");
        }

        if (getArguments() != null && getArguments().containsKey("query")) {
            query = getArguments().getString("query");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products_list, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        loading = v.findViewById(R.id.loading);
        textError = v.findViewById(R.id.textError);
        textNothing = v.findViewById(R.id.nothing);
        recyclerView = v.findViewById(R.id.recycler_view);
        adapter = new ProductsListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(App.getAppContext(), getResources().getInteger(R.integer.colomn_count)));

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
            stateIDLE();
        } else if (rvState != null) {
            restoreFromBundle(rvState);
            stateIDLE();
        } else {
            if (currCategory != null) {
                getItems(0);
            }
            if (query != null && !query.equals("")) {
                getSearchItems(0, query);
            }
        }

        return v;
    }

    private void getItems(int from) {
        cancelTasks();
        if (from == 0) {
            clearAdapter();
            stateLoading();
        }

        itemsTask = new ItemsTask(from, count, currCategory.getId(), new ItemsTask.OnGetItemsListener() {
            @Override
            public void onGetItems(ItemArrayResponse itemArrayResponse) {
                if (itemArrayResponse != null) {
                    setData(itemArrayResponse);
                } else {
                    stateError();
                }
            }
        });
        itemsTask.execute();
    }

    private void getSearchItems(int from, String query) {
        cancelTasks();
        if (from == 0) {
            clearAdapter();
            stateLoading();
        }

        searchTask = new SearchTask(from, count, query, new SearchTask.OnGetItemsListener() {
            @Override
            public void onGetItems(ItemArrayResponse itemArrayResponse) {
                if (itemArrayResponse != null) {
                    if (itemArrayResponse.getItems().size() == 0) {
                        stateNothing();
                    } else {
                        setData(itemArrayResponse);
                    }
                } else {
                    stateError();
                }
            }
        });
        searchTask.execute();
    }

    @Override
    public void onGetSearchedText(String query) {
        this.query = query;
        if (!query.equals("")) {
            getSearchItems(0, query);
        } else {
            if (currCategory != null) {
                getItems(0);
            } else {
                if (getActivity() != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onBackPressed();
                }
            }
        }
    }

    private void clearAdapter() {
        if (adapter != null)
            adapter.clear();
    }

    private void setData(ItemArrayResponse itemArrayResponse) {
        adapter.updateData(itemArrayResponse);
        adapter.notifyDataSetChanged();
        stateIDLE();
    }

    private void stateError() {
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.VISIBLE);
        textNothing.setVisibility(View.GONE);
    }

    private void stateLoading() {
        loading.setVisibility(View.VISIBLE);
        textError.setVisibility(View.GONE);
        textNothing.setVisibility(View.GONE);
    }

    private void stateNothing() {
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.GONE);
        textNothing.setVisibility(View.VISIBLE);
    }

    private void stateIDLE() {
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.GONE);
        textNothing.setVisibility(View.GONE);
    }


    @Override
    public void loadMore(int from) {
        if (query != null && !query.equals("")) {
            getSearchItems(from, query);
            return;
        }
        if (currCategory != null) {
            getItems(from);
        }
    }

    @Override
    public void onButtonBuyClick(ItemResponse item) {
        loadFragment(ProductFragment.newInstance(item), "", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showSearchButton(true);
            ((MainActivity) getActivity()).showBackButton(true);
            ((MainActivity) getActivity()).setSearchChangeListener(this);
            if(rvState != null && rvState.containsKey("isEditTextVisible")){
                ((MainActivity)getActivity()).showSearchEditText(rvState.getBoolean("isEditTextVisible"));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBackButton(false);
            ((MainActivity) getActivity()).removeSearchChangeListener();
            ((MainActivity) getActivity()).hideKeyboard(getView());
        }
        rvState = new Bundle();
        itemArrayResponse = new ItemArrayResponse();
        itemArrayResponse.setItems(adapter.getFullListData());
        rvState.putSerializable("items", itemArrayResponse);
        rvState.putParcelable("position", recyclerView.getLayoutManager().onSaveInstanceState());
        rvState.putBoolean("isEditTextVisible", ((MainActivity)getActivity()).isSearchEditTextVisible());

        cancelTasks();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        itemArrayResponse = new ItemArrayResponse();
        itemArrayResponse.setItems(adapter.getFullListData());
        outState.putSerializable("items", itemArrayResponse);
        outState.putParcelable("position", recyclerView.getLayoutManager().onSaveInstanceState());

    }

    private void cancelTasks() {
        if (itemsTask != null)
            itemsTask.cancel(true);

        if (searchTask != null)
            searchTask.cancel(true);
    }

    private void restoreFromBundle(Bundle bundle) {
        if (bundle.containsKey("items") && bundle.containsKey("position")) {
            itemArrayResponse = (ItemArrayResponse) bundle.getSerializable("items");
            if (itemArrayResponse != null && itemArrayResponse.getItems().size() == 0){
                if (currCategory != null) {
                    getItems(0);
                }
                if (query != null && !query.equals("")) {
                    getSearchItems(0, query);
                }
            } else {
                adapter.setFullListData(itemArrayResponse.getItems());
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(bundle.getParcelable("position"));
        }

    }


    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }
}
