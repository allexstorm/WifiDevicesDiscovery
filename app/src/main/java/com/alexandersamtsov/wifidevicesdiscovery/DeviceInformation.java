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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInformation extends AppCompatActivity {

    private String hostsInfo;
    private String[] hostInfoArray;
    private TextView txtHostAddress;
    private TextView txtVendorName;
    private TextView txtMac;


    private static final String TAG = "DeviceInformation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_information);

        txtHostAddress = (TextView) findViewById(R.id.deviceinformation_hostaddress);
        txtMac = (TextView) findViewById(R.id.deviceinformation_mac);
        txtVendorName = (TextView) findViewById(R.id.deviceinformation_vendorname);






        hostsInfo = getIntent().getExtras().getString("hostinfo");
        if (hostsInfo != null) {
            hostInfoArray = hostsInfo.split("\n");
        }
        txtHostAddress.setText(hostInfoArray[0]);
        txtMac.setText(hostInfoArray[2]);
        txtVendorName.setText(hostInfoArray[1]);




    }




}
