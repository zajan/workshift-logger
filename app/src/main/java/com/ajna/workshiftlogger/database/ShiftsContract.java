package com.ajna.workshiftlogger.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.ajna.workshiftlogger.database.MyContentProvider.CONTENT_AUTHORITY;
import static com.ajna.workshiftlogger.database.MyContentProvider.CONTENT_AUTHORITY_URI;

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

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
    public static long getId(Uri uri){
        return ContentUris.parseId(uri);
    }

}
