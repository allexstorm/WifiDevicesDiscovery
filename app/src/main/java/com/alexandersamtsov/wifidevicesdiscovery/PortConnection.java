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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortConnection extends AppCompatActivity {


    private static final String TAG = "PortConnection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_connection);
    }



    private void TcpConnect(String ip, int port, int timeout)
    {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean UdpConnect(String ip, int port) {


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
            return true;
        }
        catch (Exception e)
        {
            return false;
        }


    }



}
