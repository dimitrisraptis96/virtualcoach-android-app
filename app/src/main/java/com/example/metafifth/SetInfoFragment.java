package com.example.metafifth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mbientlab.metawear.UnsupportedModuleException;


public class SetInfoFragment extends FragmentBase {

    private static AppPreferences appPrefs;

    public SetInfoFragment() {
        super(1);
    }

    protected void boardReady() throws UnsupportedModuleException {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPrefs=new AppPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_info, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String previousIP = appPrefs.getValue("ip");
        String previousName = appPrefs.getValue("name");

        EditText ipEditText = (EditText)view.findViewById(R.id.ip);
        EditText nameEditText = (EditText)view.findViewById(R.id.name);

        ipEditText.setText(previousIP);
        nameEditText.setText(previousName);

        Button button = view.findViewById(R.id.submit_button);


        button.setOnClickListener(v -> {
            String ip = ipEditText.getText().toString();
            String name = nameEditText.getText().toString();
            appPrefs.setValue("ip",ip);
            appPrefs.setValue("name",name);

            Log.i("SetInfoFragment", ip);
            Log.i("SetInfoFragment", name);

        });
    }
}
