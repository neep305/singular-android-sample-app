package com.jason.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jason.myapplication.databinding.FragmentWebviewBinding;

import com.singular.sdk.Singular;
import com.singular.sdk.SingularConfig;
import com.singular.sdk.SingularJSInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebviewFragment extends Fragment {

    private FragmentWebviewBinding binding;

    private WebView webview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WebviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebviewFragment newInstance(String param1, String param2) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragement_web, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webview_main);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://singular-web-app.herokuapp.com/events");
        SingularConfig config = new SingularConfig(Constants.API_KEY, Constants.SECRET);
        Singular.init(view.getContext(), config);

        // CUSTOMIZE
        SingularWebAppInterface singularWebAppInterface = new SingularWebAppInterface(view.getContext());
        singularWebAppInterface.setWebViewId(R.id.webview_main);
        webView.addJavascriptInterface(singularWebAppInterface, "SingularInterface");

        // SINGULAR SDK
//        SingularJSInterface singularJSInterface = new SingularJSInterface(view.getContext());
//        singularJSInterface.setWebViewId(R.id.webview_main);
//
//        webView.addJavascriptInterface(singularJSInterface, "SingularInterface");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}