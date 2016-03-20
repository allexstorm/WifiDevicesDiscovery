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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInformation extends AppCompatActivity {

    private String hostsInfo;
    private String[] hostInfoArray;
    private TextView txtHostAddress;
    private TextView txtVendorName;
    private TextView txtMac;
    private TextView txtScanPort;
    private Button btnScanPort;
    private PortsScanThread portsScanThread;

    private ArrayList<String> openPorts;
    private ArrayAdapter<String> arrayAdapter;
    private ListView lstOpenPorts;
    private String host;

    private int portsToScan;
    private EditText editPortsNumber;


    private static final String TAG = "DeviceInformation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_information);

        txtHostAddress = (TextView) findViewById(R.id.deviceinformation_hostaddress);
        txtMac = (TextView) findViewById(R.id.deviceinformation_mac);
        txtVendorName = (TextView) findViewById(R.id.deviceinformation_vendorname);
        txtScanPort = (TextView) findViewById(R.id.deviceinformation_scanportstxt);
        btnScanPort = (Button) findViewById(R.id.deviceinformation_scanportsbutton);
        lstOpenPorts = (ListView) findViewById(R.id.deviceinformation_openportslist);
        editPortsNumber = (EditText) findViewById(R.id.deviceinformation_setportstoscan);



        hostsInfo = getIntent().getExtras().getString("hostinfo");
        if (hostsInfo != null) {
            hostInfoArray = hostsInfo.split("\n");
            if(hostInfoArray[0].contains("unpingable"))
            {
                String[] str = hostInfoArray[0].split(" ");
                host = str[0];
            }
            else
            {
                host = hostInfoArray[0];
            }
        }
        txtHostAddress.setText(host);
        txtMac.setText(hostInfoArray[2]);
        txtVendorName.setText(hostInfoArray[1]);
        txtScanPort.setText(getString(R.string.deviceinformation_scanportstxt));




        openPorts = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                openPorts);
        lstOpenPorts.setAdapter(arrayAdapter);

        lstOpenPorts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                showPortMenu(openPorts.get(position));

            }
        });



        btnScanPort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                portsScanThread = new PortsScanThread();
                portsScanThread.execute();

            }
        });





    }



    private class PortsScanThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            txtScanPort.setText(getString(R.string.deviceinformation_scanningports));

        }

        @Override
        protected Void doInBackground(Void... params) {


            openPorts = portsScanning();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            updatedAdapter(openPorts);
            txtScanPort.setText(getString(R.string.deviceinformation_scanningportsfinished));


        }
    }



    private ArrayList<String> portsScanning()
    {
        if(!editPortsNumber.getText().toString().matches(""))
        {
            portsToScan = Integer.valueOf(editPortsNumber.getText().toString());
        }
        else
        {
            portsToScan = 1024;
        }
        ArrayList<String> openPorts = new ArrayList<>();
        for(int i = 0; i < portsToScan; i++)
        {
            if(isPortOpenTCP(host, i, 28))
            {

                openPorts.add(String.valueOf(i) + " TCP");
                Log.d(TAG, host + " " + i + " TCP port");
            }
            if(isPortOpenUDP(host, i))
            {
                openPorts.add(String.valueOf(i) + " UDP");
                Log.d(TAG, host + " " + i + " UDP port");
            }
        }
        return openPorts;
    }

    private boolean isPortOpenTCP(String ip, int port, int timeout) {

         try {
         Socket socket = new Socket();
         socket.connect(new InetSocketAddress(ip, port), timeout);
         socket.close();
         return true;
         } catch (Exception e) {
         e.printStackTrace();
         return false;
         }

    }

    private boolean isPortOpenUDP(String ip, int port) {


         try {
         DatagramSocket dSocket = new DatagramSocket();
         dSocket.connect(new InetSocketAddress(ip, port));
         byte[] bytes = new byte[64];

             Log.d(TAG, ip + " port:" + port + " packet sending");
         dSocket.send(new DatagramPacket(bytes, bytes.length));
             dSocket.setSoTimeout(28);
             Log.d(TAG, ip + " port:" + port + " packet sending done or timeout");
             Log.d(TAG, ip + " port:" + port + " packet receiving");
         dSocket.receive(new DatagramPacket(bytes, bytes.length));
             dSocket.setSoTimeout(28);
             Log.d(TAG, ip + " port:" + port + " packet receiving done or timeout");
         dSocket.close();
            return true;
         }
         catch (Exception e)
         {
         return false;
         }


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


    private void showPortMenu(final String portTitle) {

        Port port = new Port(this);
        View mPort = port.getView();
        mPort.refreshDrawableState();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
        final AlertDialog alert = dBuilder.create();
                alert.setView(mPort);
                alert.setTitle(portTitle);
                alert.show();

        Button btnGoogleIt = (Button) mPort.findViewById(R.id.port_googleit);

        btnGoogleIt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com" + "/search?q=" + portTitle));
                startActivity(browserIntent);

            }
        });

        /**  TODO: implement tcp, udp, ssh, other connections in PortConnection class
        Button btnConnect = (Button) mPort.findViewById(R.id.port_connect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String ip = host;
                String[] portSplitted = portTitle.split(" ");
                int port = Integer.valueOf(portSplitted[0]);
                Intent intent = new Intent(DeviceInformation.this, PortConnection.class);
                intent.putExtra("ip", ip);
                intent.putExtra("port", port);
                alert.dismiss();
                //alert.cancel();
                startActivity(intent);

            }
        });**/

    }


}
