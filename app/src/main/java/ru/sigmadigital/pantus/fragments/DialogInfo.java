package ru.sigmadigital.pantus.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ru.sigmadigital.pantus.R;

public class DialogInfo extends DialogFragment implements View.OnClickListener {

    public static DialogInfo newInstance() {
        return new DialogInfo();
    }

    public static DialogInfo newInstance(String title, String message, boolean showLogo) {
        DialogInfo di =  new DialogInfo();
        di.message = message;
        di.title = title;
        di.showLogo = showLogo;
        return di;
    }


    private String title = null;
    private String message = null;
    private boolean showLogo = true;

    private TextView tvTitle, tvMessage;
    private ImageView ivLogo;

    private DialogInterface.OnDismissListener dismissListener;

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        if(this.getDialog() != null){
            this.getDialog().setOnDismissListener(dismissListener);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_info, null);

        view.findViewById(R.id.ok).setOnClickListener(this);

        ivLogo = view.findViewById(R.id.plus);
        tvTitle = view.findViewById(R.id.title);
        tvMessage = view.findViewById(R.id.text);

        if (showLogo){
            ivLogo.setVisibility(View.VISIBLE);
        }else {
            ivLogo.setVisibility(View.INVISIBLE);
        }

        if(title != null){
            tvTitle.setText(title);
        }
        if(message != null){
            tvMessage.setText(message);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setOnDismissListener(dismissListener);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
