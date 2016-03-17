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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Information extends AppCompatActivity {

    private Button btnAbout;
    private Button btnChangeLog;
    private Button btnRateThisApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);


        btnAbout = (Button) findViewById(R.id.information_about_button);
        btnChangeLog = (Button) findViewById(R.id.information_updates_log);
        btnRateThisApp = (Button) findViewById(R.id.information_rate_this_app_btn);


        btnAbout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                showLicense();

            }
        });

        btnChangeLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //showChangeLog();

            }
        });



    }

    private void showLicense() {
        License license = new License(this);
        View mLicense = license.getView();
        mLicense.refreshDrawableState();
        new AlertDialog.Builder(Information.this)
                .setView(mLicense)
                .show();
    }

    private void showChangeLog() {
        ChangeLog changelog = new ChangeLog(this);
        View mChangeLog = changelog.getView();
        mChangeLog.refreshDrawableState();
        new AlertDialog.Builder(Information.this)
                .setView(mChangeLog)
                .show();
    }

}
