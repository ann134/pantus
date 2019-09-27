package ru.sigmadigital.pantus.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import ru.sigmadigital.pantus.R;


public class WaitDialog {
    private TextView title;
    private AlertDialog dialog;
    private boolean show = false;

    public WaitDialog(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.wait_dialog, null);
        title = (TextView) v.findViewById(R.id.tv_wait_dialog_title);
        dialog = new AlertDialog.Builder(context)
                .setView(v)
                .setCancelable(false)
                .create();
    }

    public void show(String text) {
        if (!show) {
            title.setText(text);
            dialog.show();
            show = true;
        }
    }

    public void show(int text) {
        if (!show && dialog != null) {
            title.setText(text);
            dialog.show();
            show = true;
        }
    }

    public void hide() {
        if (show) {
            dialog.hide();
            show = false;
        }
    }
}
