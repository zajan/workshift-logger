package com.ajna.workshiftlogger.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MyContentProvider extends ContentProvider {

    private MyDBHelper dbHelper;
    public static final UriMatcher uriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.ajna.workshiftlogger.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final int CLIENTS = 10;
    public static final int CLIENTS_ID = 11;
    public static final int FACTORS = 20;
    public static final int FACTORS_ID = 21;
    public static final int SHIFTS = 30;
    public static final int SHIFTS_ID = 31;
    public static final int PROJECTS = 40;
    public static final int PROJECTS_ID = 41;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, ClientsContract.TABLE_NAME, CLIENTS);
        matcher.addURI(CONTENT_AUTHORITY, ClientsContract.TABLE_NAME + "/#", CLIENTS_ID);

        matcher.addURI(CONTENT_AUTHORITY, FactorsContract.TABLE_NAME, FACTORS);
        matcher.addURI(CONTENT_AUTHORITY, FactorsContract.TABLE_NAME + "/#", FACTORS_ID);

        matcher.addURI(CONTENT_AUTHORITY, ShiftsContract.TABLE_NAME, SHIFTS);
        matcher.addURI(CONTENT_AUTHORITY, ShiftsContract.TABLE_NAME + "/#", SHIFTS_ID);

        matcher.addURI(CONTENT_AUTHORITY, ProjectsContract.TABLE_NAME, PROJECTS);
        matcher.addURI(CONTENT_AUTHORITY, ProjectsContract.TABLE_NAME + "/#", PROJECTS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = MyDBHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = uriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case CLIENTS:
                queryBuilder.setTables(ClientsContract.TABLE_NAME);
                break;
            case CLIENTS_ID:
                queryBuilder.setTables(ClientsContract.TABLE_NAME);

                long clientId = ClientsContract.getId(uri);
                queryBuilder.appendWhere(ClientsContract.Columns._ID + " = " + clientId);
                break;
            case FACTORS:
                queryBuilder.setTables(FactorsContract.TABLE_NAME);
                break;
            case FACTORS_ID:
                queryBuilder.setTables(FactorsContract.TABLE_NAME);
                long factorId = FactorsContract.getId(uri);
                queryBuilder.appendWhere(FactorsContract.Columns._ID + " = " + factorId);
                break;
            case SHIFTS:
                queryBuilder.setTables(ShiftsContract.TABLE_NAME + " INNER JOIN " + ProjectsContract.TABLE_NAME
                        + " ON " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns._ID
                        + " = " + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.PROJECT_ID

                        + " INNER JOIN " + ClientsContract.TABLE_NAME
                        + " ON " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID
                        + " = " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.CLIENT_ID

                        + " LEFT JOIN " + FactorsContract.TABLE_NAME
                        + " ON " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID
                        + " = " + FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.CLIENT_ID
                );
                break;
            case SHIFTS_ID:
                queryBuilder.setTables(ShiftsContract.TABLE_NAME + " INNER JOIN " + ProjectsContract.TABLE_NAME
                        + " ON " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns._ID
                        + " = " + ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.PROJECT_ID

                        + " INNER JOIN " + ClientsContract.TABLE_NAME
                        + " ON " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID
                        + " = " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.CLIENT_ID

                        + " LEFT JOIN " + FactorsContract.TABLE_NAME
                        + " ON " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID
                        + " = " + FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.CLIENT_ID
                );
                long shiftId = ShiftsContract.getId(uri);
                queryBuilder.appendWhere(ShiftsContract.Columns._ID + " = " + shiftId);
                break;

            case PROJECTS:
                // projects are queried together with client with which they are associated
                queryBuilder.setTables(ProjectsContract.TABLE_NAME + " INNER JOIN " + ClientsContract.TABLE_NAME
                    + " ON " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.CLIENT_ID
                    + " = " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID);
                break;
            case PROJECTS_ID:
                queryBuilder.setTables(ProjectsContract.TABLE_NAME + " INNER JOIN " + ClientsContract.TABLE_NAME
                        + " ON " + ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.CLIENT_ID
                        + " = " + ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID);
                long projectId = ProjectsContract.getId(uri);
                queryBuilder.appendWhere(ProjectsContract.Columns._ID + " = " + projectId);
                break;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = uriMatcher.match(uri);
        final SQLiteDatabase db;
        Uri resultUri = null;
        long recordId;

        switch (match) {
            case CLIENTS:
                // repeated code in all switch cases to avoid accessing db
                // if the uri is not matching (performance reasons)
                db = dbHelper.getWritableDatabase();
                recordId = db.insert(ClientsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    resultUri = ClientsContract.buildUri(recordId);
                } else {
                    throw new SQLException("Failed to insert into " + uri);
                }
                break;
            case FACTORS:
                db = dbHelper.getWritableDatabase();
                recordId = db.insert(FactorsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    resultUri = FactorsContract.buildUri(recordId);
                } else {
                    throw new SQLException("Failed to insert into " + uri);
                }
                break;
            case SHIFTS:
                db = dbHelper.getWritableDatabase();
                recordId = db.insert(ShiftsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    resultUri = ShiftsContract.buildUri(recordId);
                } else {
                    throw new SQLException("Failed to insert into " + uri);
                }
                break;
            case PROJECTS:
                db = dbHelper.getWritableDatabase();
                recordId = db.insert(ProjectsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    resultUri = ProjectsContract.buildUri(recordId);
                } else {
                    throw new SQLException("Failed to insert into " + uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase db;
        int count = 0;
        String selectionCriteria;
        switch (match) {
            case CLIENTS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(ClientsContract.TABLE_NAME, selection, selectionArgs);
                break;
            case CLIENTS_ID:
                db = dbHelper.getWritableDatabase();
                long clientId = ClientsContract.getId(uri);
                selectionCriteria = ClientsContract.Columns._ID + " = " + clientId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ClientsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case FACTORS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(FactorsContract.TABLE_NAME, selection, selectionArgs);
                break;
            case FACTORS_ID:
                db = dbHelper.getWritableDatabase();
                long factorId = FactorsContract.getId(uri);
                selectionCriteria = FactorsContract.Columns._ID + " = " + factorId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(FactorsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case SHIFTS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(ShiftsContract.TABLE_NAME, selection, selectionArgs);
                break;
            case SHIFTS_ID:
                db = dbHelper.getWritableDatabase();
                long shiftId = ShiftsContract.getId(uri);
                selectionCriteria = ShiftsContract.Columns._ID + " = " + shiftId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ShiftsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case PROJECTS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(ProjectsContract.TABLE_NAME, selection, selectionArgs);
                break;
            case PROJECTS_ID:
                db = dbHelper.getWritableDatabase();
                long projectId = ProjectsContract.getId(uri);
                selectionCriteria = ProjectsContract.Columns._ID + " = " + projectId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ProjectsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase db;
        int count = 0;
        String selectionCriteria;
        switch (match) {
            case CLIENTS:
                db = dbHelper.getWritableDatabase();
                count = db.update(ClientsContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case CLIENTS_ID:
                db = dbHelper.getWritableDatabase();
                long clientId = ClientsContract.getId(uri);
                selectionCriteria = ClientsContract.Columns._ID + " = " + clientId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ClientsContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;
            case FACTORS:
                db = dbHelper.getWritableDatabase();
                count = db.update(FactorsContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case FACTORS_ID:
                db = dbHelper.getWritableDatabase();
                long factorId = FactorsContract.getId(uri);
                selectionCriteria = FactorsContract.Columns._ID + " = " + factorId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(FactorsContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;
            case SHIFTS:
                db = dbHelper.getWritableDatabase();
                count = db.update(ShiftsContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case SHIFTS_ID:
                db = dbHelper.getWritableDatabase();
                long shiftId = ShiftsContract.getId(uri);
                selectionCriteria = ShiftsContract.Columns._ID + " = " + shiftId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ShiftsContract.TABLE_NAME, contentValues,selectionCriteria, selectionArgs);
                break;
            case PROJECTS:
                db = dbHelper.getWritableDatabase();
                count = db.update(ProjectsContract.TABLE_NAME, contentValues,selection, selectionArgs);
                break;
            case PROJECTS_ID:
                db = dbHelper.getWritableDatabase();
                long projectId = ProjectsContract.getId(uri);
                selectionCriteria = ProjectsContract.Columns._ID + " = " + projectId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ProjectsContract.TABLE_NAME, contentValues,selectionCriteria, selectionArgs);
                break;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}
