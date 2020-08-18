package com.georgeampartzidis.greenfridge.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.georgeampartzidis.greenfridge.data.ProductsContract.ProductsEntry;

/**
 * Created by georgeampartzidis on 27/01/2018.
 */

public class ProductsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 5;

    /**
     * String that will be used to create the products table
     */
    private static final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsEntry.TABLE_NAME
            + " (" + ProductsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ProductsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
            + ProductsEntry.COLUMN_EXPIRY_DATE + " INTEGER NOT NULL);";

    /**
     * String that will be used to create the products_list table
     */
    private static final String SQL_CREATE_PRODUCTS_LIST_TABLE = "CREATE TABLE " +
            ProductsContract.ProductsListEntry.TABLE_NAME + " (" +
            ProductsContract.ProductsListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ProductsContract.ProductsListEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL);";

    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsContract.ProductsListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
