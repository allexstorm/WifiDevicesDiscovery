/*
 *     Copyright (C) 2016  Alexander Samtsov
 *
 *     This file is part of Wifi Devices Discovery.
 *
 *     Wifi Devices Discovery is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Wifi Devices Discovery is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Wifi Devices Discovery.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alexandersamtsov.wifidevicesdiscovery;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Set;

public class SavedData {


    private static final String PREFS_NAME = "myprefs";

    private static final String PREFS_NETWORK_NAME= "network_name";
    private static final String PREFS_DATA= "data";
    private static final String PREFS_HOSTNAME= "hostname";
    private static final String PREFS_MAC= "mac";
    private static final String PREFS_VENDOR= "vendor";


    private static final String PREFS_254MODE= "mode";


    public SavedData() {
        super();
    }


    public void saveNetworkName(Context context, String networkName)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_NETWORK_NAME, networkName);
        editor.apply();
    }


    public String loadNetworkName(Context context)
    {
        SharedPreferences settings;
        String name;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        name = settings.getString(PREFS_NETWORK_NAME, "default");
        return name;
    }

    public void saveData(Context context, Set<String> data)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putStringSet(PREFS_DATA, data);
        editor.apply();
    }

    public Set<String> loadData(Context context)
    {
        SharedPreferences settings;
        Set<String> setDefault = null;
        Set<String> set;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        set = settings.getStringSet(PREFS_DATA, setDefault);
        return set;
    }


    public void save254Mode(Context context, int mode)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putInt(PREFS_254MODE, mode);
        editor.apply();
    }

    public int load254Mode(Context context)
    {
        SharedPreferences settings;
        int mode;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mode = settings.getInt(PREFS_254MODE, 1);
        return mode;
    }




}
