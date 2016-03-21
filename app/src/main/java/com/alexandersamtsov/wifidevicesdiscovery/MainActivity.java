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
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private TextView txtSsid;
    private TextView txtMyIpAddr;
    private TextView txtDisplay;
    private TextView txtScan254;
    private ArrayList<String> hosts;
    private ArrayAdapter<String> arrayAdapter;
    private Button btnScan;
    private String checkIp;
    private String myIp;
    private String myMac;
    private VendorsDbHelper myDbHelper;
    private String ssid;
    private Button btnSettings;

    private Context context = this;


    private ListView lstDevList;

    private NetworkCheck networkCheck;
    private NetworkCheckAll networkCheckAll;
    //private NetworkCheckAllPing networkCheckAllPing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSsid = (TextView) findViewById(R.id.mainactivity_ssid);
        txtMyIpAddr = (TextView) findViewById(R.id.mainactivity_myipaddr);
        txtScan254 = (TextView) findViewById(R.id.mainactivity_texformscan254);
        txtDisplay = (TextView) findViewById(R.id.mainactivity_texform);
        lstDevList = (ListView) findViewById(R.id.mainactivity_devlist);
        btnScan = (Button) findViewById(R.id.mainactivity_scan);
        btnSettings = (Button) findViewById(R.id.mainactivity_settings_button);




        myDbHelper = new VendorsDbHelper(this);
        try {
            myDbHelper.createDatabase();
        } catch (Exception e) {
            Log.e(TAG, "can't create db", e);
        }
        try {
            myDbHelper.openDatabase();
        } catch (Exception e) {
            Log.e(TAG, "can't open db", e);
        }




        WifiManager manager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info=manager.getConnectionInfo();
        ssid = info.getSSID();
        myMac = info.getMacAddress();
        int myIpAddress = info.getIpAddress();
        checkIp  = String.format("%d.%d.%d.", (myIpAddress & 0xff), (myIpAddress >> 8 & 0xff), (myIpAddress >> 16 & 0xff));
        myIp = String.format("%d.%d.%d.%d", (myIpAddress & 0xff), (myIpAddress >> 8 & 0xff), (myIpAddress >> 16 & 0xff), (myIpAddress >> 24 & 0xff));


        txtSsid.setText(ssid);
        txtMyIpAddr.setText(String.format(getString(R.string.mainactivity_displaymyip), myIp));
        txtDisplay.setText(getString(R.string.status_default));
        hosts = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                hosts);
            lstDevList.setAdapter(arrayAdapter);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Information.class);
                startActivity(intent);

            }
        });


        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                    networkCheckAll = new NetworkCheckAll();
                    networkCheckAll.execute();



                /**  very slow and freeze on some devices
                else
                {
                    networkCheckAllPing = new NetworkCheckAllPing();
                    networkCheckAllPing.execute();
                }**/



            }
        });




        lstDevList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                String hostInfo = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, DeviceInformation.class);
                intent.putExtra("hostinfo", hostInfo);
                startActivity(intent);

            }
        });



    }



    private class NetworkCheckAll extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            txtScan254.setText(String.format(getString(R.string.mainactivity_reaching), checkIp));

        }

        @Override
        protected Void doInBackground(Void... params) {


            scanAll();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            txtScan254.setText(String.format(getString(R.string.mainactivity_finishedreaching), checkIp));
            networkCheck = new NetworkCheck();
            networkCheck.execute();


        }
    }


    /**private class NetworkCheckAllPing extends AsyncTask<Void, Void, Void> {

        //@Override
        protected void onPreExecute() {
            super.onPreExecute();

            txtScan254.setText(String.format(getString(R.string.mainactivity_pinging), checkIp));

        }

        //@Override
        protected Void doInBackground(Void... params) {


            pingAll();

            return null;
        }

        //@Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            txtScan254.setText(String.format(getString(R.string.mainactivity_finishedpinging), checkIp));


        }
    }**/

    private void scanAll()
    {
        InetAddress inetAddress;
        for (int i = 1; i < 255; i++)
        {
            try {
                inetAddress = InetAddress.getByName(checkIp + i);
                try {
                    inetAddress.isReachable(28);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            //ifHostUp(checkIp + i);
            Log.d(TAG, checkIp + i);
        }
    }

    /**private void pingAll()
    {
        InetAddress inetAddress;
        for (int i = 1; i < 255; i++)
        {
            ifHostUp(checkIp + i);
            Log.d(TAG, checkIp + i);
        }
    }**/




    private class NetworkCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtDisplay.setText(getString(R.string.status_searching));


        }

        @Override
        protected Void doInBackground(Void... params) {



            hosts = scanSubNet();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /**
            try {
                SavedData data = new SavedData();
                HashSet<String> set = new HashSet<>(hosts);
                data.saveIt(context, set);
            }
            catch (Exception e)
            {
                Log.e(TAG, "qqqq", e);
            }**/

            updatedAdapter(hosts);
            txtDisplay.setText(String.format(getString(R.string.status_finished), hosts.size()));





        }
    }


    private ArrayList<String> scanSubNet(){

        ArrayList<String> ips = DeviceHelper.getIps();
        ArrayList<String> hosts = new ArrayList<>();
        InetAddress inetAddress;
        String hostAddr;

        if(ifHostUp(myIp))
        {
            try {
                inetAddress = InetAddress.getByName(myIp);
                String macPart = myMac.substring(0, 8);
                hosts.add(inetAddress.getHostAddress() + "\n" + myMac.toUpperCase() + "\n" + myDbHelper.getVendor(macPart.toUpperCase()));
                Log.d(TAG, inetAddress.getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        for(int i=0; i < ips.size(); i++){
            Log.e(TAG, "Trying: " + ips.get(i));
            try {
                hostAddr = ips.get(i);
                inetAddress = InetAddress.getByName(hostAddr);
                if(ifHostUp(hostAddr) && !myIp.equals(hostAddr)){
                    String macPart = DeviceHelper.getMac(inetAddress.getHostAddress()).substring(0, 8);
                    hosts.add(inetAddress.getHostAddress() + "\n" + DeviceHelper.getMac(inetAddress.getHostAddress()).toUpperCase() + "\n" + myDbHelper.getVendor(macPart.toUpperCase()));
                    Log.d(TAG, inetAddress.getHostAddress());
                }
                else if(!ifHostUp(hostAddr) && !myIp.equals(hostAddr)){
                    String macPart = DeviceHelper.getMac(inetAddress.getHostAddress()).substring(0, 8);
                    hosts.add(inetAddress.getHostAddress()  + " unpingable" + "\n" + DeviceHelper.getMac(inetAddress.getHostAddress()).toUpperCase() + "\n" + myDbHelper.getVendor(macPart.toUpperCase()));
                    Log.d(TAG, inetAddress.getHostAddress());
                }

            }  catch (IOException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(hosts);

        ArrayList<String> unp = new ArrayList<>();
        for (Iterator<String> it = hosts.iterator(); it.hasNext();) {
            String str = it.next();
            if (str.contains("unpingable")) {
                it.remove();
                unp.add(str);
            }
        }

        for (String str : unp)
        {
            hosts.add(str);
        }


        return hosts;
    }






    private void updatedAdapter(ArrayList<String> itemsArrayList) {

        arrayAdapter.clear();

        if (itemsArrayList != null){

            for (String str : itemsArrayList) {

                arrayAdapter.add(str);
            }
        }

        arrayAdapter.notifyDataSetChanged();

    }





    private boolean ifHostUp(String host){
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process process = runtime.exec("/system/bin/ping -q -w 1 -c 1 " + host); // -q quit mode, -w deadline in seconds, -c packets count
            int exitValue = process.waitFor();
            return exitValue == 0;
        }
        catch (InterruptedException e)
        {
            Log.d(TAG, "interrupted", e);
        }
        catch (IOException e)
        {
            Log.d(TAG, "IOException caught", e);
        }
        return ifHostUp(host);
    }





}
