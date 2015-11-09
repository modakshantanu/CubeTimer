package com.shantanu.cubetimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by Bharat Modak on 6/11/2015.
 */
public class Category {

    Puzzle puzzle;
    String name;
    boolean predefined;

    void setValues(String name,Puzzle puzzle,boolean predefined){
        this.name = name;
        this.puzzle = puzzle;
        this.predefined = predefined;
    }

    void saveToPrefs(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("_"+name+"PUZZLE",puzzle.id);
        editor.putString("_"+name,name);
        editor.putBoolean("_"+name+"PREDEFINED",predefined);
        editor.apply();
    }

    void getFromPrefs(String name,Context context){
        this.name = name;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        puzzle = Puzzle.getById(preferences.getInt("_"+name+"PUZZLE",2));
        predefined = preferences.getBoolean("_"+name+"PREDEFINED",false);


    }



}
