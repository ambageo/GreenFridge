package com.georgeampartzidis.greenfridge.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.georgeampartzidis.greenfridge.data.ProductsContract.ProductsEntry;
import com.georgeampartzidis.greenfridge.data.ProductsContract.ProductsListEntry;

/**
 * Created by georgeampartzidis on 29/01/2018.
 */

public class ProductContentProvider extends ContentProvider {

    public static final String LOG_TAG = ProductContentProvider.class.getSimpleName();
    private ProductsDbHelper mProductsDbHelper;

    // define final integer constraints for the whole product table and a single product item
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_WITH_ID = 101;
    private static final int PRODUCT_WITH_EXPIRY_DATE = 102;

    private static final int PRODUCTS_LIST = 200;
    private static final int PRODUCTS_LIST_WITH_ID = 201;

    // Declare a static variable for the Uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher with no matches
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /* All paths added to the UriMatcher have a corresponding int.
        *  For each kind of uri we want to match, we add the corresponding match */

        /* First, the paths for the products table */
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCT, PRODUCTS);
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY,
                ProductsContract.PATH_PRODUCT + "/#", PRODUCT_WITH_ID);
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY,
                ProductsContract.PATH_PRODUCT + "/#", PRODUCT_WITH_EXPIRY_DATE);

        /* Then, the paths for the productsList table */
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY,
                        ProductsContract.PATH_PRODUCTS_LIST, PRODUCTS_LIST);
        uriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY,
                ProductsContract.PATH_PRODUCTS_LIST + "/#", PRODUCTS_LIST_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mProductsDbHelper = new ProductsDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // First, get access to the database
        final SQLiteDatabase db = mProductsDbHelper.getReadableDatabase();

        // Take the int code of the uri whose path we will match against.
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match) {
            case PRODUCTS: {
                returnCursor = db.query(ProductsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case PRODUCT_WITH_EXPIRY_DATE: {
                String expiryDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{expiryDateString};
                returnCursor = db.query(ProductsEntry.TABLE_NAME,
                        projection,
                        ProductsEntry.COLUMN_EXPIRY_DATE + " < ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case PRODUCTS_LIST: {
                returnCursor= db.query(ProductsListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        // We set a notification uri on the Cursor and return it
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mProductsDbHelper.getWritableDatabase();
        long id;
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case PRODUCTS:
                id = db.insert(ProductsEntry.TABLE_NAME, null, values);
                // Check whether the new values were inserted properly
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ProductsEntry.PRODUCTS_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case PRODUCTS_LIST:
                id= db.insert(ProductsContract.ProductsListEntry.TABLE_NAME, null, values);
                if (id>0) {
                    returnUri= ContentUris.withAppendedId(ProductsListEntry.PRODUCTS_LIST_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver if the uri has been changed
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mProductsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedProducts;

        // getPathSegments explanation in T09.05-QueryAllTasks. It gets the second segment
        // of the path (and last one), which is the product id.
        String id = uri.getPathSegments().get(1);
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{id};
        switch (match) {
            case PRODUCT_WITH_ID:
                deletedProducts = db.delete(ProductsEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            case PRODUCTS_LIST_WITH_ID:
                deletedProducts= db.delete(ProductsListEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        // Notify the resolver of the change
        if (deletedProducts != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedProducts;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
