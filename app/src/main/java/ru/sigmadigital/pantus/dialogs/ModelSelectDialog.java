package ru.sigmadigital.pantus.dialogs;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.adapters.ModelsSelectAdapter;

public class ModelSelectDialog  {

    private AlertDialog dialog;
    private RecyclerView list;
    private ModelsSelectAdapter adapter;

    public ModelSelectDialog(Context context, final OnModelSelectListener modelSelectListener, String[] models) {
        View v = View.inflate(context, R.layout.select_item_dialog, null);
        list = v.findViewById(R.id.rv_models);
        list.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ModelsSelectAdapter(models, context, new ModelsSelectAdapter.OnModelSelectListener() {
            @Override
            public void onModelSelect(String model) {
                if(modelSelectListener != null){
                    modelSelectListener.onModelSelect(model);
                    dialog.dismiss();
                }
            }
        });
        list.setAdapter(adapter);
        dialog = new AlertDialog.Builder(context)
                .setView(v)

                .show();
    }

    public interface OnModelSelectListener{
        void onModelSelect(String name);
    }
}
