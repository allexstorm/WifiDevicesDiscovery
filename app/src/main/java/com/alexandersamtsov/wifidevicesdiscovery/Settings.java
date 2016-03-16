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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends AppCompatActivity {


    private RadioGroup choose254Mode;
    private RadioButton fast;
    private RadioButton slow;
    private int modeId;
    private Context context = this;

    private Button btnSaveMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btnSaveMode = (Button) findViewById(R.id.settings_save254mode_button);
        choose254Mode = (RadioGroup) findViewById(R.id.settings_choose254mode);
        fast = (RadioButton) findViewById(R.id.settings_254fast_mode);
        slow = (RadioButton) findViewById(R.id.settings_254slow_mode);


        SavedData data = new SavedData();

            if (data.load254Mode(this) == 1) {
                choose254Mode.check(R.id.settings_254fast_mode);
                modeId = 1;
            } else if (data.load254Mode(this) == 2) {
                choose254Mode.check(R.id.settings_254slow_mode);
                modeId = 2;
            }


        choose254Mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (fast.isChecked()) {
                    modeId = 1;
                } else {
                    modeId = 2;
                }


            }
        });




        btnSaveMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SavedData data = new SavedData();
                data.save254Mode(context, modeId);

            }
        });



    }
}
