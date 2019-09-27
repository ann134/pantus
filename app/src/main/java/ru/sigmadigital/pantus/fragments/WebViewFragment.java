package ru.sigmadigital.pantus.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.activities.MainActivity;

public class WebViewFragment extends BaseFragment {

    private String url;
    private ProgressBar progressBar;
    private TextView textError;
    private TextView textBlank;
    private WebView webView;

    static WebViewFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url", url);
        Log.e("url_put", url);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("url")) {
            url = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_web_view, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        progressBar = v.findViewById(R.id.loading);
        textError = v.findViewById(R.id.textError);
        textBlank = v.findViewById(R.id.textBlank);
        webView = v.findViewById(R.id.web_view);
        setWebClient();
        Log.e("url", url);
        webView.loadUrl(url);

        return v;
    }


    private void setWebClient() {
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("OverrideUrlLoading", "load url:" + url);

                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("onPageStarted", url);

                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("onPageFinished", url);

                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                if (url.equals("about:blank")) {
                    textBlank.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("onReceivedError", url);

                progressBar.setVisibility(View.GONE);
                textError.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showCloseButton(true);
            ((MainActivity) getActivity()).showBottomNav(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showCloseButton(false);
            ((MainActivity) getActivity()).showBottomNav(true);
        }
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content;
    }
}