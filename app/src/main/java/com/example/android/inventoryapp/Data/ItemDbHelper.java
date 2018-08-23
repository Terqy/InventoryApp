package com.example.android.inventoryapp.Data;

import android.content.ClipData;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "items.db";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME +  "("
                + ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemContract.ItemEntry.COLUMN_PRODUCT_NAME + " TEXT, "
                + ItemContract.ItemEntry.COLUMN_PRICE + " INTEGER, "
                + ItemContract.ItemEntry.COLUMN_QUANTITY + " INTEGER, "
                + ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT,"
                + ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT DEFAULT 0);";


        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
