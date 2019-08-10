package com.example.metafifth;


import android.os.Bundle;

import android.util.Log;
import android.webkit.ServiceWorkerClient;
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

    private static AppPreferences appPrefs;

    private final int layoutId;
    final String PUBLIC_URL = "https://flex-your-muscle.netlify.com/";
    final String DEV_URL = "http://192.168.1.8" + ":8000";

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

        appPrefs=new AppPreferences(getActivity());
        String ip = appPrefs.getValue("ip");
        String name = appPrefs.getValue("name");

        Log.i("SensorFragment", ip);
        Log.i("SensorFragment", name);

        mWebView = view.findViewById(R.id.webview);
        mWebView.loadUrl(ip);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Button clearButton = view.findViewById(R.id.clear_button);
        clearButton.setText(R.string.label_clear);
        Button saveButton= view.findViewById(R.id.save_button);
        saveButton.setText(R.string.label_save);

        //Button calibrateButton= view.findViewById(R.id.calibrate);
        //calibrateButton.setOnClickListener(view2->calibrate());

        Switch switched = view.findViewById(R.id.sample_control);
        clearButton.setOnClickListener(v -> {
            clean();
            switched.setChecked(false);// Code here executes on main thread after user presses button
        });

        saveButton.setOnClickListener(v -> {
            mWebView.post(() -> mWebView.evaluateJavascript("javascript: " + "saveData( " + name +" )", null));
        });

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
    //protected abstract void calibrate();
}
