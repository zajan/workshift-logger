package com.ajna.workshiftlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper{
    private static final String TAG = "MyDBHelper";
    private static String DATABASE_NAME = "WorkshiftLogger.db";
    private static final int DATABASE_VERSION = 1;
    private static MyDBHelper instance = null;

    private MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    static MyDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sql;
        sql = "CREATE TABLE " + ClientsContract.TABLE_NAME + " (" +
                ClientsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ClientsContract.Columns.NAME+ " TEXT NOT NULL, " +
                ClientsContract.Columns.OFFICIAL_NAME+ " TEXT, " +
                ClientsContract.Columns.ADDRESS + " TEXT, " +
                ClientsContract.Columns.BASE_PAYMENT + " INTEGER NOT NULL, " +
                ClientsContract.Columns.PAY_TYPE + " INTEGER NOT NULL);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE " + FactorsContract.TABLE_NAME + " (" +
                FactorsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                FactorsContract.Columns.CLIENT_ID+ " INTEGER NOT NULL, " +
                FactorsContract.Columns.START_HOUR+ " INTEGER NOT NULL, " +
                FactorsContract.Columns.VALUE+ " INTEGER NOT NULL);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE " + ProjectsContract.TABLE_NAME + " (" +
                ProjectsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ProjectsContract.Columns.NAME + " TEXT NOT NULL, " +
                ProjectsContract.Columns.CLIENT_ID+ " INTEGER NOT NULL);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE " + ShiftsContract.TABLE_NAME + " (" +
                ShiftsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ShiftsContract.Columns.START_TIME+ " INTEGER NOT NULL, " +
                ShiftsContract.Columns.END_TIME+ " INTEGER, " +
                ShiftsContract.Columns.PAUSE + " INTEGER, " +
                ShiftsContract.Columns.PROJECT_ID + " INTEGER);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
