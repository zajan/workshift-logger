package com.ajna.workshiftlogger.database;

import android.provider.BaseColumns;

public class ShiftsContract {
    static final String TABLE_NAME = "Shifts";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String START_TIME = "StartTime";
        public static final String END_TIME = "EndTime";
        public static final String PAUSE = "Pause";
        public static final String PROJECT_ID = "ProjectId";
        public static final String CLIENT_ID = "ClientId";
    }

}
