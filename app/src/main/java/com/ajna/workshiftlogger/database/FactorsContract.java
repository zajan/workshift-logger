package com.ajna.workshiftlogger.database;

import android.provider.BaseColumns;

public class FactorsContract {
    static final String TABLE_NAME = "Factors";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String CLIENT_ID = "ClientId";
        public static final String START_HOUR = "StartHour";
        public static final String VALUE = "Value";
    }
}
