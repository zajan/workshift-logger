package com.ajna.workshiftlogger.database;

import android.provider.BaseColumns;

public class ClientsContract {
    static final String TABLE_NAME = "Clients";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "Name";
        public static final String OFFICIAL_NAME = "OfficialName";
        public static final String ADDRESS = "Address";
        public static final String BASE_PAYMENT = "BasePayment";
        public static final String PAY_TYPE = "PayType";
    }
}