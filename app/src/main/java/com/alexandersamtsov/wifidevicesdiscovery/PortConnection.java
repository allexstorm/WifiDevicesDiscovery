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

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;


public class PortConnection extends AppCompatActivity {


    private static final String TAG = "PortConnection";

    private EditText editTcp;
    private Button btnTcp;
    private EditText editUdp;
    private Button btnUdp;

    private String ip;
    private int port;

    UdpThread udpThread;
    TcpThread tcpThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_connection);

        editTcp = (EditText) findViewById(R.id.portconnection_writeheretcp);
        btnTcp = (Button) findViewById(R.id.portconnection_sendtcp);
        editUdp = (EditText) findViewById(R.id.portconnection_writehereudp);
        btnUdp = (Button) findViewById(R.id.portconnection_sendudp);

        ip = getIntent().getExtras().getString("ip");
        port = getIntent().getExtras().getInt("port");

        btnTcp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                tcpThread = new TcpThread();
                tcpThread.execute();

            }
        });

        btnUdp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                udpThread = new UdpThread();
                udpThread.execute();


            }
        });



    }

    private class TcpThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast toast = Toast.makeText(getApplicationContext(),
                    "TCP Sending...", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        protected Void doInBackground(Void... params) {


            TcpSend(ip, port, 100);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Toast toast = Toast.makeText(getApplicationContext(),
                    "TCP Sent!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class UdpThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast toast = Toast.makeText(getApplicationContext(),
                    "UDP Sending...", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        protected Void doInBackground(Void... params) {


            UdpSend(ip, port);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Toast toast = Toast.makeText(getApplicationContext(),
                    "UDP Sent!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }




    private void TcpSend(String ip, int port, int timeout)
    {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            //PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.print(editTcp.getText());
            output.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, ip + " port:" + port + " e caught", e);
        }
    }

    private void UdpSend(String ip, int port) {


        try {
            DatagramSocket dSocket = new DatagramSocket();
            dSocket.connect(new InetSocketAddress(ip, port));
            byte[] bytes = new byte[64];

            Log.d(TAG, ip + " port:" + port + " packet sending");
            dSocket.send(new DatagramPacket(bytes, bytes.length));
            dSocket.setSoTimeout(20);
            Log.d(TAG, ip + " port:" + port + " packet sending done or timeout");
            Log.d(TAG, ip + " port:" + port + " packet receiving");
            dSocket.receive(new DatagramPacket(bytes, bytes.length));
            dSocket.setSoTimeout(20);
            Log.d(TAG, ip + " port:" + port + " packet receiving done or timeout");
            dSocket.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, ip + " port:" + port + " e caught");
        }


    }



}
