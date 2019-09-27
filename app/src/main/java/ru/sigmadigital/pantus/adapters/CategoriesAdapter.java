package ru.sigmadigital.pantus.adapters;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.models.CategoryArrayResponse;
import ru.sigmadigital.pantus.models.CategoryResponse;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryHolder> {

    private Picasso picasso = Picasso.get();


    private OnCategoryClickListener listener;
    private List<CategoryResponse> categoryList = new ArrayList<>();


    public CategoriesAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setData(CategoryArrayResponse categoryArrayResponse) {
        this.categoryList = categoryArrayResponse.getCategories();
    }

    public void setData(List<CategoryResponse> categories) {
        this.categoryList = categories;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, null);
        int pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, parent.getContext().getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxHeight);
        int pxMarginsHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, parent.getContext().getResources().getDisplayMetrics());
        params.setMargins(pxMarginsHorizontal, 0, pxMarginsHorizontal, 0);
        v.setLayoutParams(params);
        return new CategoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, final int position) {
        final CategoryResponse category = categoryList.get(position);

        picasso.load(Network.BASE_URL + "/api/categories/" + category.getId() + "/img").into(holder.categoryImage);
        holder.categoryName.setText(category.getName());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryClick(category);
            }
        });
    }

    public List<CategoryResponse> getCategoryList() {
        return categoryList;
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    class CategoryHolder extends RecyclerView.ViewHolder {
        LinearLayout card;
        ImageView categoryImage;
        TextView categoryName;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.ll_card);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryResponse categoryResponse);
    }
}
