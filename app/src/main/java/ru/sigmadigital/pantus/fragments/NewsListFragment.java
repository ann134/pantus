package ru.sigmadigital.pantus.fragments;

import android.os.Bundle;
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
import ru.sigmadigital.pantus.adapters.NewsAdapter;
import ru.sigmadigital.pantus.api.asynctask.NewsTask;
import ru.sigmadigital.pantus.models.NewsArrayResponse;
import ru.sigmadigital.pantus.models.NewsResponse;


public class NewsListFragment extends BaseFragment implements NewsAdapter.OnNewsClickListener, NewsAdapter.LoadMoreListener {

    private int count = 10;

    private NewsTask newsTask;
    private NewsArrayResponse newsArrayResponse = new NewsArrayResponse();

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ProgressBar loading;
    private TextView textError;

    private Bundle rvState;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_list, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        loading = v.findViewById(R.id.loading);
        textError = v.findViewById(R.id.textError);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(App.getAppContext(), getResources().getInteger(R.integer.colomn_count)));
        adapter = new NewsAdapter(this, this, new NewsArrayResponse());
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else if (rvState != null) {
            restoreFromBundle(rvState);
        } else {
            getNews(0);
        }

        return v;
    }


    private void getNews(int from) {
        newsTask = new NewsTask(from, count, new NewsTask.OnGetNewsListener() {
            @Override
            public void onGetNews(NewsArrayResponse news) {
                if (news != null) {
                    newsArrayResponse.update(news);
                    addNewsToAdapter(news);
                } else {
                    error();
                }
            }
        });
        newsTask.execute();
    }

    private void addNewsToAdapter(NewsArrayResponse news) {
        adapter.addNews(news);
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.GONE);
    }

    private void error() {
        loading.setVisibility(View.GONE);
        textError.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadMore(int from) {
        getNews(from);
    }

    @Override
    public void onNewsClick(NewsResponse newsItem) {
        loadFragment(NewsItemFragment.newInstance(newsItem), "", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showSearchButton(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (newsTask != null)
            newsTask.cancel(true);

        rvState = new Bundle();
        rvState.putSerializable("news", newsArrayResponse);
        if (recyclerView.getLayoutManager() != null) {
            rvState.putParcelable("position", recyclerView.getLayoutManager().onSaveInstanceState());
        }


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("news", newsArrayResponse);
        if (recyclerView.getLayoutManager() != null) {
            outState.putParcelable("position", recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    private void restoreFromBundle(Bundle bundle){
        if(bundle.containsKey("news") && bundle.containsKey("position")) {
            newsArrayResponse = (NewsArrayResponse) bundle.getSerializable("news");
            if (newsArrayResponse.getNews().size() == 0){
                getNews(0);
            } else {
                addNewsToAdapter(newsArrayResponse);
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(bundle.getParcelable("position"));
        }
    }



    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }

}
