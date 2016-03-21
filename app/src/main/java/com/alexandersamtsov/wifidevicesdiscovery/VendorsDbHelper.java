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
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VendorsDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static final String DATABASE_NAME = "vendors.db";
    private static final String TABLE_NAME = "vendors";

    public static final String ID_COLUMN_NAME = "_id";
    public static final String MAC_COLUMN_NAME = "mac";
    public static final String VENDOR_COLUMN_NAME = "vendor";


    private static String DATABASE_PATH = "";
    private static final int DATABASE_VERSION = 2;
    public static final int DATABASE_VERSION_old = 1;

    //Constructor
    public VendorsDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DATABASE_PATH = myContext.getDatabasePath(DATABASE_NAME).toString();
    }




    //Create a empty database on the system
    public void createDatabase() throws IOException
    {
        boolean dbExist = checkDataBase();
        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }
        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error copying database", e);
            }
        }
    }


    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(Exception e)
        {
            Log.e(TAG, "exception while checking db", e);
        }
        return checkDB;
    }



    //Copies your database from your local assets-folder to the just created empty database in the system folder

    private void copyDataBase() throws IOException
    {
        String outFileName = DATABASE_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }



    //delete database
    public void db_delete()
    {
        File file = new File(DATABASE_PATH);
        if(file.exists())
        {
            file.delete();
            Log.v(TAG,"delete database file.");
        }
    }


    //Open database
    public void openDatabase() throws SQLException
    {
        String myPath = DATABASE_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db)
    {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }



    public String getVendor(String mac)
    {
        String macEdit = mac.substring(0,2) + mac.substring(3,5) + mac.substring(6,mac.length());
        SQLiteDatabase db = this.getWritableDatabase();
        String vendor;
        Cursor c = db.rawQuery("SELECT vendor FROM vendors WHERE mac = '"+macEdit+"'", null);
        if(c.moveToFirst()) {
            vendor = c.getString(c.getColumnIndex("vendor"));
        }
        else
        {
            vendor = "unknown";
        }
        c.close();
        return vendor;
    }

    public String getSpecVendor(String mac)
    {
        String macEdit = mac.substring(0,2) + mac.substring(3,5) + mac.substring(6,mac.length());
        SQLiteDatabase db = this.getWritableDatabase();
        String vendor;
        Cursor c = db.rawQuery("SELECT vendor FROM vendors WHERE mac = 'E04F43'", null);
        c.moveToFirst();
        vendor = c.getString(c.getColumnIndex("vendor"));
        c.close();
        return vendor;
    }


}
