package de.soft4mg.sudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtil {

    Context context;
    SharedPreferences preferences;

    public PrefUtil(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    String getString(int id){
        return preferences.getString(context.getString(id),null);
    }
    String getString(int id, String defValue){
        return preferences.getString(context.getString(id),defValue);
    }
    String putString(int id, String newValue){
        preferences.edit().putString(context.getString(id),newValue).apply();
        return newValue;
    }

    int getInt(int id){
        return preferences.getInt(context.getString(id),0);
    }
    int getInt(int id, int devValue){
        return preferences.getInt(context.getString(id),devValue);
    }
    int putInt(int id, int newValue){
        preferences.edit().putInt(context.getString(id),newValue).apply();
        return newValue;
    }

    boolean getBoolean(int id){
        return preferences.getBoolean(context.getString(id),false);
    }
    boolean getBoolean(int id, boolean devValue){
        return preferences.getBoolean(context.getString(id),devValue);
    }
    boolean putBoolean(int id, boolean newValue){
        preferences.edit().putBoolean(context.getString(id),newValue).apply();
        return newValue;
    }

}
