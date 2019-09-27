package ru.sigmadigital.pantus.adapters;

import android.annotation.SuppressLint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.models.ItemArrayResponse;
import ru.sigmadigital.pantus.models.ItemResponse;

public class ProductsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private long allCount;
    private List<ItemResponse> itemsList = new ArrayList<>();
    private OnButtonBuyListener listener;
    private LoadMoreListener loadMoreListener;


    public ProductsListAdapter(OnButtonBuyListener listener, LoadMoreListener loadMoreListener) {
        this.listener = listener;
        this.loadMoreListener = loadMoreListener;
    }



    public void updateData(ItemArrayResponse itemArrayResponse) {
        int posStart = getItemCount();
        this.itemsList.addAll(itemArrayResponse.getItems());
        this.allCount = itemArrayResponse.getAllCount();
        //notifyDataSetChanged();
        notifyItemRangeChanged(posStart, getItemCount());
    }

    public List<ItemResponse> getFullListData(){
        return itemsList;
    }

    public void setFullListData(List<ItemResponse> list){
        this.itemsList = list;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case ItemType.item:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, null);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new ProductHolder(v);
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
        if (holder instanceof ProductHolder) {
            final ItemResponse item = itemsList.get(position);

            Picasso.get()
                    .load(Network.BASE_URL + "/api/items/" + item.getId() + "/img")
                    .placeholder(R.drawable.placeholder_logo)
                    .error(R.drawable.placeholder_logo)
                    .into(((ProductHolder) holder).productImage);

            ((ProductHolder) holder).productName.setText(item.getName() + " " + item.getId());

            ((ProductHolder) holder).price.setText(item.getPrice());
            ((ProductHolder) holder).currency.setText(App.getAppContext().getString(R.string.rub));
            ((ProductHolder) holder).count.setText(item.getCountAtStock() + App.getAppContext().getString(R.string.comp));

            setDoubleColoredText(((ProductHolder) holder).brand, App.getAppContext().getString(R.string.brand), item.getBrand());
            setDoubleColoredText(((ProductHolder) holder).category, App.getAppContext().getString(R.string.category), item.getCategoryName());
            setDoubleColoredText(((ProductHolder) holder).article, App.getAppContext().getString(R.string.articl), item.getArticle());
            setDoubleColoredText(((ProductHolder) holder).oem, App.getAppContext().getString(R.string.oem), item.getOem());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onButtonBuyClick(item);
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

    private void setDoubleColoredText(TextView tv, String s1, String s2) {
        int color1 = App.getAppContext().getResources().getColor(R.color.black);
        int color2 = App.getAppContext().getResources().getColor(R.color.colorAccent);

        ForegroundColorSpan fcs1 = new ForegroundColorSpan(color1);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(color2);
        Spannable spannable = new SpannableString(s1 + s2);
        spannable.setSpan(fcs1, 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(fcs2, s1.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannable);
    }

    public void clear() {
        this.itemsList.clear();
        this.allCount = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (itemsList.size() == 0)
            return itemsList.size();
        return itemsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return ItemType.last;
        return ItemType.item;
    }


    //holders
    private static class ItemType {
        private static final int item = 9;
        private static final int last = 4;
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;

        TextView price;
        TextView currency;
        TextView count;

        TextView brand;
        TextView category;
        TextView article;
        TextView oem;

        ProductHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);

            price = itemView.findViewById(R.id.price);
            currency = itemView.findViewById(R.id.currency);
            count = itemView.findViewById(R.id.count);

            brand = itemView.findViewById(R.id.tv_brand);
            category = itemView.findViewById(R.id.tv_category);
            article = itemView.findViewById(R.id.tv_article);
            oem = itemView.findViewById(R.id.tv_oem);
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


    public interface OnButtonBuyListener {
        void onButtonBuyClick(ItemResponse item);
    }

    public interface LoadMoreListener {
        void loadMore(int from);
    }
}
