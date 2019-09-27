package ru.sigmadigital.pantus.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;

public class ContactsFragment extends BaseFragment implements View.OnClickListener  {


    private CardView mapContainer;
    private ImageView ivMap;

    private static final int MY_STORAGE_REQUEST_CODE = 200;


    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        mapContainer = v.findViewById(R.id.cardView);
        ivMap = v.findViewById(R.id.iv_map);
        ivMap.setOnClickListener(this);
        return v;
    }






    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showSearchButton(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private int convertDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_map){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/maps/11143/balakovo/?l=map&ll=47.828505%2C51.994658&mode=whatshere&source=wizgeo&utm_medium=maps-desktop&utm_source=serp&whatshere%5Bpoint%5D=47.819632%2C51.996770&whatshere%5Bzoom%5D=18&z=14"));
            startActivity(browserIntent);
        }
    }
}
