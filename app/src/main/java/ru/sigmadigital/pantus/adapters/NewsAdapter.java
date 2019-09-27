package ru.sigmadigital.pantus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.models.NewsArrayResponse;
import ru.sigmadigital.pantus.models.NewsResponse;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int allCount;
    private List<NewsResponse> news;
    private OnNewsClickListener listener;
    private LoadMoreListener loadMoreListener;

    public NewsAdapter(OnNewsClickListener listener, LoadMoreListener loadMoreListener, NewsArrayResponse news) {
        this.allCount = news.getAllCount();
        this.news = news.getNews();
        this.loadMoreListener = loadMoreListener;
        this.listener = listener;
    }

    public void addNews(NewsArrayResponse news) {
        int posStart = getItemCount();
        this.news.addAll(news.getNews());
        this.allCount = news.getAllCount();
        //notifyDataSetChanged();
        notifyItemRangeChanged(posStart, getItemCount());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case ItemType.news:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new NewsHolder(v);
            case ItemType.last:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new LoadingHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new LoadingHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsHolder) {
            final NewsResponse newsItem = news.get(position);

            Picasso.get()
                    .load(Network.BASE_URL + "/api/news/" + newsItem.getId() + "/img")
                    .placeholder(R.drawable.placeholder_logo)
                    .error(R.drawable.placeholder_logo)
                    .into(((NewsHolder) holder).image);

            ((NewsHolder) holder).date.setText(newsItem.getNewsDate());
            ((NewsHolder) holder).title.setText(newsItem.getLabel());

            ((NewsHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNewsClick(newsItem);
                }
            });
        }

        if (holder instanceof LoadingHolder) {
            if (position < allCount) {
                loadMoreListener.loadMore(position);
            } else {
                /*((LoadingHolder) holder).end.setVisibility(View.VISIBLE);*/
                ((LoadingHolder) holder).loading.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (news.size() == 0)
            return news.size();
        return news.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return ItemType.last;
        return ItemType.news;
    }


    //holders
    private static class ItemType {
        private static final int news = 3;
        private static final int last = 4;
    }

    class NewsHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView date;
        TextView title;

        NewsHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {
        ProgressBar loading;
        /*TextView end;*/

        LoadingHolder(@NonNull View itemView) {
            super(itemView);
            loading = itemView.findViewById(R.id.loading);
            /*end = itemView.findViewById(R.id.end);*/
        }
    }


    public interface OnNewsClickListener {
        void onNewsClick(NewsResponse newsItem);
    }

    public interface LoadMoreListener {
        void loadMore(int from);
    }

}