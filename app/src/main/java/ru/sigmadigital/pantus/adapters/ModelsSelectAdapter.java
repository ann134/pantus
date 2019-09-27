package ru.sigmadigital.pantus.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.sigmadigital.pantus.R;

public class ModelsSelectAdapter extends RecyclerView.Adapter<ModelsSelectAdapter.ModelHolder> {

    private String[] models;
    private Context context;
    private OnModelSelectListener listener;

    public ModelsSelectAdapter(String[] models, Context context, OnModelSelectListener listener) {
        this.models = models;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(context, R.layout.model_item, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ModelHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelHolder holder, final int position) {
        holder.name.setText(models[position]);
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onModelSelect(models[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.length;
    }

    static class ModelHolder extends RecyclerView.ViewHolder {

        View v;
        TextView name;

        public ModelHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            name = v.findViewById(R.id.tv_name);
        }
    }

    public interface OnModelSelectListener {
        void onModelSelect(String model);
    }
}
