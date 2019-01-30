package com.ajna.workshiftlogger.database;

import android.provider.BaseColumns;

public class ProjectsContract {
    static final String TABLE_NAME = "Projects";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "Name";
        public static final String CLIENT_ID = "ClientId";
    }

}
