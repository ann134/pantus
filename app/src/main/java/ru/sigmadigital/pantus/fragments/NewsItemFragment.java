package ru.sigmadigital.pantus.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.models.NewsResponse;

public class NewsItemFragment extends BaseFragment implements View.OnClickListener {

    private NewsResponse newsItem;

    static NewsItemFragment newInstance(NewsResponse newsItem) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("news")) {
            newsItem = (NewsResponse) getArguments().getSerializable("news");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_news_item, null);

        ImageView image = v.findViewById(R.id.image);

        Picasso.get()
                .load(Network.BASE_URL + "/api/news/" + newsItem.getId() + "/img")
                .placeholder(R.drawable.placeholder_logo)
                .error(R.drawable.placeholder_logo)
                .into(image);

        TextView date = v.findViewById(R.id.date);
        date.setText(newsItem.getNewsDate());
        TextView title = v.findViewById(R.id.title);
        title.setText(newsItem.getLabel());
        WebView text = v.findViewById(R.id.text);
        text.loadData(newsItem.getText(),"text/html","utf-8");
        v.findViewById(R.id.bt_chain).setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBackButton(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBackButton(false);
        }
    }

    @Override
    public void onClick(View v) {
        loadFragment(WebViewFragment.newInstance(newsItem.getLink()), "", true);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }

}