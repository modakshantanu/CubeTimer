package com.shantanu.cubetimer;


import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {



    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }


}
