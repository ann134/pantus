package ru.sigmadigital.pantus.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;
import ru.sigmadigital.pantus.activities.PlayerActivity;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.models.ItemResponse;


public class ProductFragment extends BaseFragment implements View.OnClickListener {

    private ItemResponse currItem;

    private ImageView productImage;
    private TextView productName;
    private TextView price;
    private TextView count;
    private TextView codePantus;
    private TextView maker;
    private TextView country;
    private TextView article;
    private TextView applicability;
    private TextView weight;
    private Button buyButton;
    private ImageButton playButton;
    private ImageButton linkButton;


    public static ProductFragment newInstance(ItemResponse item) {
        ProductFragment fragment = new ProductFragment();
        fragment.setCurrItem(item);
        return fragment;
    }

    private void setCurrItem(ItemResponse item) {
        this.currItem = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_product, null);

        if(savedInstanceState != null && savedInstanceState.containsKey("item")){
            currItem = (ItemResponse) savedInstanceState.getSerializable("item");
        }

        productImage = v.findViewById(R.id.product_image);

        Picasso.get()
                .load(Network.BASE_URL + "/api/items/" + currItem.getId() + "/img")
                .placeholder(R.drawable.placeholder_logo)
                .error(R.drawable.placeholder_logo)
                .into(productImage);

        productName = v.findViewById(R.id.tv_product_name);
        productName.setText(currItem.getName());

        price = v.findViewById(R.id.tv_price);
        price.setText(currItem.getPrice());
        count = v.findViewById(R.id.tv_count);

        count.setText(getString(R.string.rub) + "/ " + currItem.getCountAtStock() + getString(R.string.sht));

        codePantus = v.findViewById(R.id.tv_code_pantus);
        maker = v.findViewById(R.id.tv_maker);
        country = v.findViewById(R.id.tv_country);
        article = v.findViewById(R.id.tv_article);

        codePantus.setText(currItem.getCode());
        maker.setText(currItem.getBrand());
        country.setText(currItem.getCountry());
        article.setText(currItem.getArticle());

        applicability = v.findViewById(R.id.tv_applicability);
        weight = v.findViewById(R.id.tv_weight);

        applicability.setText(currItem.getApplicability());
        weight.setText(currItem.getWeight());

        buyButton = v.findViewById(R.id.btn_buy);
        playButton = v.findViewById(R.id.bt_play);
        linkButton = v.findViewById(R.id.bt_link);

        playButton.setOnClickListener(this);
        linkButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBackButton(true);
            ((MainActivity) getActivity()).showSearchButton(false);
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

            case R.id.bt_play:
                Intent intent = new Intent(App.getAppContext(), PlayerActivity.class);
                intent.putExtra("video_link", currItem.getVideo());
                startActivity(intent);
                break;

            case R.id.bt_link:
                loadFragment(WebViewFragment.newInstance(currItem.getLink()), "", true);
                break;

            case R.id.btn_buy:
                loadFragment(OrderFormFragment.newInstance(currItem), "", true);
                break;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item", currItem);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }

}
