package com.example.marcin.osmtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marcin on 15.11.2016.
 */

public class MySQLiteHelper extends SQLiteOpenHelper
{
        public static final String TABLE_ADDRESSES = "addresses";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ADDRES = "addres";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";


        private static final String DATABASE_NAME = "addresses.db";
        private static final int DATABASE_VERSION = 1;

        // Database creation sql statement
        private static final String DATABASE_CREATE = "create table if not exists "
                + TABLE_ADDRESSES + "( " + COLUMN_ID
                + " integer primary key autoincrement, " + COLUMN_ADDRES
                + " text not null, " + COLUMN_LATITUDE + " REAL, " + COLUMN_LONGITUDE + " REAL);";

        public MySQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESSES);
            onCreate(db);
        }

    }


