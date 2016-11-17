package com.example.marcin.osmtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddressesDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ADDRES, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE};

    public AddressesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public DatabaseAddress createDatabaseAddress(String comment, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADDRES, comment);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        long insertId = database.insert(MySQLiteHelper.TABLE_ADDRESSES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADDRESSES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        DatabaseAddress newDatabaseAddress = cursorToAddresses(cursor);
        cursor.close();
        return newDatabaseAddress;
    }

    public void deleteAddressInDatabase(DatabaseAddress address) {
        long id = address.getId();
        database.delete(MySQLiteHelper.TABLE_ADDRESSES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<DatabaseAddress> getAllAddressesFromDatabase() {
        List<DatabaseAddress> addresses = new ArrayList<DatabaseAddress>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADDRESSES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DatabaseAddress address = cursorToAddresses(cursor);
            addresses.add(address);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return addresses;
    }

    private DatabaseAddress cursorToAddresses(Cursor cursor) {
        DatabaseAddress address = new DatabaseAddress();
        address.setId(cursor.getLong(0));
        address.setAddress(cursor.getString(1));
        address.setLatitude(cursor.getDouble(2));
        address.setLongitude(cursor.getDouble(3));
        return address;
    }
}