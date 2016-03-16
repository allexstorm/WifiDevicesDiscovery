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

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class DeviceHelper {


    private static final String TAG = "DeviceHelper";





    public static String getMac(String ip) {
        if (ip != null) {
            BufferedReader br = null;

            try {
                br = new BufferedReader(new FileReader("/proc/net/arp"));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found", e);
            }
            String line;
            try {
                if (br != null) {
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");
                        if (splitted.length >= 4 && ip.equals(splitted[0])) {
                            String mac = splitted[3];
                            if (mac.matches("..:..:..:..:..:..")) {
                                return mac;
                            } else {
                                return null;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static ArrayList<String> getIps() {

        ArrayList<String> ips = new ArrayList<>();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader("/proc/net/arp"));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found", e);
            }
            String line;
            try {
                if (br != null) {
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");
                        if (splitted.length >= 4 && ((splitted[2].equals("0x2")) || (splitted[2].equals("0x0")))) {
                            String mac = splitted[3];
                            if (!mac.matches("00:00:00:00:00:00")) {
                                ips.add(splitted[0]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        Collections.sort(ips);
        return ips;
    }


}
