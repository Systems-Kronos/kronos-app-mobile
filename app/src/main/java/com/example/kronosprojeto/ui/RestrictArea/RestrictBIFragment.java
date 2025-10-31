package com.example.kronosprojeto.ui.RestrictArea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.kronosprojeto.R;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

public class RestrictBIFragment extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restrict_b_i, container, false);

        webView = view.findViewById(R.id.webview_bi);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());

        String url = "https://app.powerbi.com/view?r=eyJrIjoiZTcyNjZjYWEtYTI5YS00NTA1LWE5MmYtMmM2MTVkMmRlYWZhIiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9";
        webView.loadUrl(url);


        ImageView imgBack = view.findViewById(R.id.imgBack);

        imgBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.HomeFragment);
        });

        return view;
    }
}