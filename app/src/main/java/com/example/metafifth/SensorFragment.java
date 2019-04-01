package com.example.metafifth;


import android.os.Bundle;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Switch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.mbientlab.metawear.Route;
public abstract class SensorFragment extends FragmentBase {

    protected float min, max;
    protected Route streamRoute = null;


    private final int layoutId;
    final String PUBLIC_URL = "https://flex-your-muscle.netlify.com/";
    final String DEV_URL = "http://192.168.1.6" + ":8000/";

    static WebView mWebView = null;


    protected SensorFragment(int sensorResId, int layoutId, float min, float max) {
        super(sensorResId);
        this.layoutId= layoutId;
        this.min= min;
        this.max= max;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View v= inflater.inflate(layoutId, container, false);
        return v;
    }



    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.webview);
        mWebView.loadUrl(DEV_URL);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Button clearButton= view.findViewById(R.id.layout_two_button_left);
        clearButton.setText(R.string.label_clear);
        clearButton.setOnClickListener(view1 -> clean());

        ((Switch) view.findViewById(R.id.sample_control)).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                setup();
            }
            else{
                clean();
            }
        });

    }


    protected abstract void setup();
    protected abstract void clean();
}
