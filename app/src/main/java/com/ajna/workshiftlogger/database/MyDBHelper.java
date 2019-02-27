package com.ajna.workshiftlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDBHelper";
    private static String DATABASE_NAME = "WorkshiftLogger.db";
    private static final int DATABASE_VERSION = 2;
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
                ClientsContract.Columns.NAME + " TEXT NOT NULL, " +
                ClientsContract.Columns.OFFICIAL_NAME + " TEXT, " +
                ClientsContract.Columns.ADDRESS + " TEXT, " +
                ClientsContract.Columns.BASE_PAYMENT + " INTEGER NOT NULL, " +
                ClientsContract.Columns.PAY_TYPE + " INTEGER NOT NULL);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE " + FactorsContract.TABLE_NAME + " (" +
                FactorsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                FactorsContract.Columns.CLIENT_ID + " INTEGER NOT NULL, " +
                FactorsContract.Columns.START_HOUR + " INTEGER NOT NULL, " +
                FactorsContract.Columns.VALUE + " INTEGER NOT NULL);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE " + ProjectsContract.TABLE_NAME + " (" +
                ProjectsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ProjectsContract.Columns.NAME + " TEXT NOT NULL, " +
                ProjectsContract.Columns.CLIENT_ID + " INTEGER NOT NULL);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE " + ShiftsContract.TABLE_NAME + " (" +
                ShiftsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ShiftsContract.Columns.START_TIME + " INTEGER NOT NULL, " +
                ShiftsContract.Columns.END_TIME + " INTEGER, " +
                ShiftsContract.Columns.PAUSE + " INTEGER, " +
                ShiftsContract.Columns.PROJECT_ID + " INTEGER);";
        Log.d(TAG, "onCreate: " + sql);
        db.execSQL(sql);

        addShiftFullInfoView(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                addShiftFullInfoView(sqLiteDatabase);
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
    }

    private void addShiftFullInfoView(SQLiteDatabase db){
        /*
        CREATE VIEW vwShiftFullInfo AS SELECT
        Shifts.StartTime,
        Shifts.EndTime,
        Shifts.Pause,
        Projects.Name,
        Clients.Name,
        Clients.OfficialName,
        Clients.Address,
        Clients.Payment,
        Clients.PaymentTime,
        Factors.Hours,
        Factors.Value
        FROM Shifts
        INNER JOIN Projects ON Projects._id = Shifts.ProjectId
        INNER JOIN Clients ON Clients._id = Projects.ClientId
        LEFT JOIN Factors ON Clients._id = Factors.ClientId;
         */

        String sql;

        sql = "CREATE VIEW " + ShiftFullInfoViewContract.TABLE_NAME + " AS SELECT "
                + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns._ID + " AS " + ShiftFullInfoViewContract.Columns._ID + ", "
                + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.START_TIME + " AS " + ShiftFullInfoViewContract.Columns.START_TIME + ", "
                + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.END_TIME + " AS " + ShiftFullInfoViewContract.Columns.END_TIME + ", "
                + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.PAUSE + " AS " + ShiftFullInfoViewContract.Columns.PAUSE + ", "
                + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME + " AS " + ShiftFullInfoViewContract.Columns.PROJECT_NAME + ", "
                + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " AS " + ShiftFullInfoViewContract.Columns.CLIENT_NAME + ", "
                + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.OFFICIAL_NAME + " AS " + ShiftFullInfoViewContract.Columns.CLIENT_OFFICIAL_NAME + ", "
                + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.ADDRESS + " AS " + ShiftFullInfoViewContract.Columns.CLIENT_ADDRESS + ", "
                + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.BASE_PAYMENT + " AS " + ShiftFullInfoViewContract.Columns.BASE_PAYMENT + ", "
                + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.PAY_TYPE + " AS " + ShiftFullInfoViewContract.Columns.PAYMENT_TYPE + ", "
                + FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.START_HOUR + " AS " + ShiftFullInfoViewContract.Columns.FACTOR_HOUR + ", "
                + FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.VALUE + " AS " + ShiftFullInfoViewContract.Columns.FACTOR_VALUE

                + " FROM " + ShiftsContract.TABLE_NAME
                + " INNER JOIN " + ProjectsContract.TABLE_NAME
                + " ON " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns._ID
                + " = " + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.PROJECT_ID

                + " INNER JOIN " + ClientsContract.TABLE_NAME
                + " ON " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID
                + " = " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.CLIENT_ID

                + " LEFT JOIN " + FactorsContract.TABLE_NAME
                + " ON " + FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.CLIENT_ID + ";";
        Log.d(TAG, "addShiftFullInfoView: sql = " + sql);
        db.execSQL(sql);

    }
}
