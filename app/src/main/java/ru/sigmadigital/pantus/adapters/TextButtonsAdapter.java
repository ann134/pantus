package ru.sigmadigital.pantus.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.models.ItemTextResponse;
import ru.sigmadigital.pantus.util.CircleTransform;

public class TextButtonsAdapter extends RecyclerView.Adapter<TextButtonsAdapter.TextButtonHolder> {

    private List<ItemTextResponse> elements;
    private Context context;
    private OnTextClickListener listener;
    private int selected = -1;

    public TextButtonsAdapter(List<ItemTextResponse> elements, Context context, OnTextClickListener listener) {
        this.elements = elements;
        this.context = context;
        this.listener = listener;
    }

    public void setElements(List<ItemTextResponse> elements) {
        int size = this.elements.size();
        selected = -1;
        this.elements.clear();

        notifyItemRangeRemoved(0, size);

        this.elements = elements;
        notifyItemRangeInserted(0, elements.size());
    }

    @NonNull
    @Override
    public TextButtonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(context, R.layout.text_item_button, null);
        int a = context.getResources().getDimensionPixelOffset(R.dimen.text_button_a);
        int b = context.getResources().getDimensionPixelOffset(R.dimen.text_button_b);
        v.setLayoutParams(new LinearLayout.LayoutParams(a, b));
        return new TextButtonHolder(v);
    }

    public void disableSelected() {
        if (selected != -1) {
            int old = selected;
            selected = -1;
            notifyItemChanged(old);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TextButtonHolder holder, final int position) {
        holder.llButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == position) {
                    int old = selected;
                    selected = -1;
                    notifyItemChanged(old);
                } else {
                    if (selected != -1) {
                        int old = selected;
                        notifyItemChanged(old);
                    }
                    selected = position;
                }
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onTextCkick(elements.get(position).getText(), position);

                }
            }
        });
        if (elements.get(position).getImgLink() != null && elements.get(position).getImgLink().length() > 0) {
            Picasso.get()
                    .load(Network.BASE_URL + "/" + elements.get(position).getImgLink())
                    .transform(new CircleTransform())
                    .into(holder.ibButton);
        } else {
            holder.ibButton.setImageResource(R.drawable.ic_star_white_24dp);
        }
        if (selected == position) {
            holder.llButton.setBackground(context.getDrawable(R.drawable.bt_oval_r_selected));
        } else {
            holder.llButton.setBackground(context.getDrawable(R.drawable.bt_oval_red));
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface OnTextClickListener {
        void onTextCkick(String text, int pos);
    }

    static class TextButtonHolder extends RecyclerView.ViewHolder {

        ImageView ibButton;
        LinearLayout llButton;

        public TextButtonHolder(@NonNull View itemView) {
            super(itemView);
            ibButton = itemView.findViewById(R.id.bt_text);
            llButton = itemView.findViewById(R.id.ll_button);
        }
    }
}
