package ru.sigmadigital.pantus.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ru.sigmadigital.maskededittext.MaskedEditText;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.models.ItemResponse;

public class OrderFormFragment extends BaseFragment implements View.OnClickListener {

    private ItemResponse item;

    private EditText name;
    private MaskedEditText phone;
    private EditText email;
    private EditText count;
    private EditText city;
    private EditText adress;
    private EditText comment;

    public static OrderFormFragment newInstance(ItemResponse item) {
        OrderFormFragment fragment = new OrderFormFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("item")) {
            item = (ItemResponse) getArguments().getSerializable("item");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_order_form, null);

        if (savedInstanceState != null && savedInstanceState.containsKey("item")) {
            item = (ItemResponse) savedInstanceState.getSerializable("item");
        }


        name = v.findViewById(R.id.editTextName);
        phone = v.findViewById(R.id.editTextPhone);
        email = v.findViewById(R.id.editTextMail);
        count = v.findViewById(R.id.editTextСount);
        count.setText("1");
        adress = v.findViewById(R.id.editTextAdress);
        city = v.findViewById(R.id.editTextCity);
        comment = v.findViewById(R.id.editTextComment);

        v.findViewById(R.id.button_send).setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.button_send:
                checkForms();
                break;
        }
    }

    private void checkForms() {

        if (name.getText().toString().equals("")) {
            showDialog("Некорректно введено ФИО");
            return;
        }

        String p = phone.getText().toString();
        p = p.replaceAll("\\s+","");
        if (p.length() < 14) {
            showDialog("Некорректно введен номер телефона");
            return;
        }


        Log.e("kjhb", email.getText().toString()+"");

        if (!isValidEmail(email.getText().toString())) {
            showDialog("Некорректный E-mail");
            return;
        }

        if (count.getText().toString().equals("")) {
            showDialog("Введите количество");
            return;
        }

        if (city.getText().toString().equals("")) {
            showDialog("Укажите город");
            return;
        }

        if (adress.getText().toString().equals("")) {
            showDialog("Укажите адресс");
            return;
        }

        sendEmail();
    }

    private void sendEmail() {
        String textbody;
        StringBuilder builder = new StringBuilder();

        builder.append(getString(R.string.fio)).append(":");
        builder.append(name.getText().toString()).append(";\n");

        builder.append(getString(R.string.phone_hint)).append(":");
        builder.append(phone.getText().toString()).append(";\n");

        builder.append(getString(R.string.e_mail)).append(":");
        builder.append(email.getText().toString()).append(";\n");

        //товар
        builder.append("Товар").append(":");
        builder.append(item.getName()).append(";\n");

        builder.append("id").append(":");
        builder.append(item.getId()).append(";\n");

        builder.append(getString(R.string.article)).append(":");
        builder.append(item.getArticle()).append(";\n");

        builder.append(getString(R.string.codePantus)).append(":");
        builder.append(item.getCode()).append(";\n");
        //end товар

        builder.append(getString(R.string.count)).append(":");
        builder.append(count.getText().toString()).append(";\n");

        builder.append(getString(R.string.city)).append(":");
        builder.append(city.getText().toString()).append(";\n");

        builder.append(getString(R.string.adress_hint)).append(":");
        builder.append(adress.getText().toString()).append(";\n");

        if (comment.getText().length() != 0) {
            builder.append(getString(R.string.comment)).append(":");
            builder.append(comment.getText().toString()).append(";\n");
        }

        textbody = builder.toString();


        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_send)});
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.theme_email);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, textbody);

        try {
            startActivity(Intent.createChooser(intent, "action"));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    private static boolean isValidEmail(CharSequence target) {
        if (target == null || target.equals("")) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void showDialog(String textError) {
        DialogOrderError dialogFragment = DialogOrderError.newInstance(textError);
        dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);

        if (getFragmentManager() != null)
            dialogFragment.show(getFragmentManager(), "DialogCounter");
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item", item);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }
}
