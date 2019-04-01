package com.example.metafifth;


import android.os.Bundle;

import android.view.View;

import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.data.Quaternion;
import com.mbientlab.metawear.module.SensorFusionBosch;
import com.mbientlab.metawear.module.SensorFusionBosch.*;


public class SensorFusionFragment extends SensorFragment {

    private SensorFusionBosch sensorFusion;
    private int srcIndex = 0;

    public SensorFusionFragment() {
        super(R.string.navigation_fragment_sensor_fusion, R.layout.fragment_sensor_spinner, -1f, 1f);
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    protected void setup() {
        sensorFusion.configure()
                .mode(Mode.NDOF)
                .accRange(AccRange.AR_16G)
                .gyroRange(GyroRange.GR_2000DPS)
                .commit();

        if (srcIndex == 0) {
            sensorFusion.quaternion().addRouteAsync(source -> source.stream((data, env) -> {
                final Quaternion quaternion = data.value(Quaternion.class);
                double w=quaternion.w();
                double x=quaternion.x();
                double y=quaternion.y();
                double z=quaternion.z();

                mWebView.post(() -> mWebView.evaluateJavascript("javascript: " + "updateQuaternion(" + x + "," + y + "," + z + "," + w + ")", null));

            })).continueWith(task -> {
                streamRoute = task.getResult();
                sensorFusion.quaternion().start();
                sensorFusion.start();

                return null;
            });
        }
    }

    @Override
    protected void clean() {
    sensorFusion.stop();
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException {
        sensorFusion = mwBoard.getModuleOrThrow(SensorFusionBosch.class);
    }

}
