package com.example.metafifth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.DialogFragment;

import android.bluetooth.BluetoothDevice;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;


import android.support.v7.widget.Toolbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.MenuItem;
import android.widget.Button;


import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Settings;
import com.example.metafifth.FragmentBase.FragmentBus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.metafifth.ScannerActivity.setConnInterval;


import bolts.Continuation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentBus, ServiceConnection {



    public final static String EXTRA_BT_DEVICE= "com.example.metasecond.MainActivity.EXTRA_BT_DEVICE",
                               FRAGMENT_KEY= "com.example.meta.FRAGMENT_KEY";

   private final static Map<Integer, Class<? extends FragmentBase>> FRAGMENT_CLASSES;
   static {
       Map<Integer, Class<? extends FragmentBase>> tempMap= new LinkedHashMap<>();
          tempMap.put(R.id.nav_home, HomeFragment.class);
          tempMap.put(R.id.nav_sensor_fusion,SensorFusionFragment.class);
        FRAGMENT_CLASSES= Collections.unmodifiableMap(tempMap);
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



    public static class ReconnectDialogFragment extends DialogFragment implements ServiceConnection {
        private static final String KEY_BLUETOOTH_DEVICE= "com.mbientlab.metawear.app.NavigationActivity.ReconnectDialogFragment.KEY_BLUETOOTH_DEVICE";



        private ProgressDialog reconnectDialog = null;
        private BluetoothDevice btDevice= null;
        private MetaWearBoard currentMwBoard= null;

        public static ReconnectDialogFragment newInstance(BluetoothDevice btDevice) {
            Bundle args= new Bundle();
            args.putParcelable(KEY_BLUETOOTH_DEVICE, btDevice);

            ReconnectDialogFragment newFragment= new ReconnectDialogFragment();
            newFragment.setArguments(args);

            return newFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            btDevice= getArguments().getParcelable(KEY_BLUETOOTH_DEVICE);
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), BtleService.class), this, BIND_AUTO_CREATE);

            reconnectDialog = new ProgressDialog(getActivity());
            reconnectDialog.setTitle(getString(R.string.title_reconnect_attempt));
            reconnectDialog.setMessage(getString(R.string.message_wait));
            reconnectDialog.setCancelable(false);
            reconnectDialog.setCanceledOnTouchOutside(false);
            reconnectDialog.setIndeterminate(true);
            reconnectDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.label_cancel), (dialogInterface, i) -> {
                currentMwBoard.disconnectAsync();
                getActivity().finish();
            });

            return reconnectDialog;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            currentMwBoard= ((BtleService.LocalBinder) service).getMetaWearBoard(btDevice);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }

    }

    private MetaWearBoard mwBoard;
    private Fragment currentFragment= null;
    private BluetoothDevice btDevice;
    private final String RECONNECT_DIALOG_TAG= "reconnect_dialog_tag";
    private final Handler taskScheduler = new Handler();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button disc = findViewById(R.id.disconnect);
        disc.setOnClickListener(view->((FragmentBase) currentFragment).mwBoard.disconnectAsync());


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
       } else {
           currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
       }



        btDevice = getIntent().getParcelableExtra(EXTRA_BT_DEVICE);
        getApplicationContext().bindService(new Intent(this, BtleService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, FRAGMENT_KEY, currentFragment);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Class fragmentClass = null;
        Fragment fragment;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_home) {
            fragmentClass = HomeFragment.class;
        }
        if (id == R.id.nav_sensor_fusion) {
            fragmentClass = SensorFusionFragment.class;
        }

        String fragmentTag= FRAGMENT_CLASSES.get(id).getCanonicalName();
        fragment= fragmentManager.findFragmentByTag(fragmentTag);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //if user presses back button, if drawer is open it closes, if drawer is closed mboard disconnects
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mwBoard.disconnectAsync();
            super.onBackPressed();
        }
    }

    private Continuation<Void, Void> reconnectResult= task -> {
        ((android.support.v4.app.DialogFragment) getSupportFragmentManager().findFragmentByTag(RECONNECT_DIALOG_TAG)).dismiss();

        if (task.isCancelled()) {
            finish();
        } else {
            setConnInterval(mwBoard.getModule(Settings.class));
            ((com.example.metafifth.FragmentBase) currentFragment).reconnected();
        }

        return null;
    };

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    private void attemptReconnect() {
        attemptReconnect(0);
    }

    private void attemptReconnect(long delay) {
        ReconnectDialogFragment dialogFragment= ReconnectDialogFragment.newInstance(btDevice);
        dialogFragment.show(getSupportFragmentManager(), RECONNECT_DIALOG_TAG);

        if (delay != 0) {
            taskScheduler.postDelayed(() -> ScannerActivity.reconnect(mwBoard).continueWith(reconnectResult), delay);
        } else {
            ScannerActivity.reconnect(mwBoard).continueWith(reconnectResult);
        }
    }




}
