package ru.sigmadigital.pantus.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.sigmadigital.pantus.R;

public class ModelsAdapter extends RecyclerView.Adapter<ModelsAdapter.ModelHolder> {

    private Context context;
    private OnModelClickListener listener;
    private int count = 0;

    public ModelsAdapter(Context context, OnModelClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(context, R.layout.model_pos_item, null);
        int a = context.getResources().getDimensionPixelOffset(R.dimen.text_button_a);
        int b = context.getResources().getDimensionPixelOffset(R.dimen.text_button_b);
        v.setLayoutParams(new LinearLayout.LayoutParams(a, b));
        return new ModelHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelHolder holder, final int position) {
        holder.btButton.setText((position + 1) + "");
        holder.btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onModelClick(position);
                }
            }
        });
    }

    public void setCount(int c) {
        if (count > 0) {
            notifyItemRangeRemoved(0, count);
        }
        count = c;
        notifyItemRangeInserted(0, count);
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public interface OnModelClickListener {
        void onModelClick(int pos);
    }

    static class ModelHolder extends RecyclerView.ViewHolder {

        Button btButton;

        public ModelHolder(@NonNull View itemView) {
            super(itemView);
            btButton = itemView.findViewById(R.id.bt_text);
        }
    }
}
