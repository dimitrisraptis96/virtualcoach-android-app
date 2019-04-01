package com.example.metafifth;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.module.Switch;


import java.util.Locale;

import bolts.Task;

public class HomeFragment extends FragmentBase {
    private Led ledModule;
    private int switchRouteId = -1;


    public HomeFragment() {
        super(R.string.navigation_fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.board_rssi_text).setOnClickListener(v -> mwBoard.readRssiAsync()
                .continueWith(task -> {
                    ((TextView) view.findViewById(R.id.board_rssi_value)).setText(String.format(Locale.US, "%d dBm", task.getResult()));
                    return null;
                }, Task.UI_THREAD_EXECUTOR)
        );
        view.findViewById(R.id.board_battery_level_text).setOnClickListener(v -> mwBoard.readBatteryLevelAsync()
                .continueWith(task -> {
                    ((TextView) view.findViewById(R.id.board_battery_level_value)).setText(String.format(Locale.US, "%d", task.getResult()));
                    return null;
                }, Task.UI_THREAD_EXECUTOR)
        );
    }
    @Override
    protected void boardReady() throws UnsupportedModuleException {
        setupFragment(getView());
    }
    private void setupFragment(final View v) {

        if (!mwBoard.isConnected()) {
            return;
        }

        mwBoard.readDeviceInformationAsync().continueWith(task -> {
            if (task.getResult() != null) {
                ((TextView) v.findViewById(R.id.device_mac_address_value)).setText(mwBoard.getMacAddress());
            }

            return null;
        }, Task.UI_THREAD_EXECUTOR);


    }
}
