package com.georgeampartzidis.greenfridge;

import androidx.loader.content.CursorLoader;
import androidx.loader.app.LoaderManager;
import android.content.Intent;
import androidx.loader.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.georgeampartzidis.greenfridge.data.ProductsContract;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;
import com.georgeampartzidis.greenfridge.sync.ExpiringProductsReminderUtilities;
import com.georgeampartzidis.greenfridge.utilities.ProductDateUtilities;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final int ID_DATES_LOADER = 11;

    private SQLiteDatabase mDb;
    private int mNumberOfProducts;
    private Cursor mCursor;
    private TextView mFridgeContentTextView;
    private static long todayDate;
    private static String deviceLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        deviceLocale = Locale.getDefault().getLanguage();

        ProductsDbHelper dbHelper = new ProductsDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mFridgeContentTextView = findViewById(R.id.tv_fridge_content);
        mCursor = getAllProducts();
        mNumberOfProducts = mCursor.getCount();
        showNumberOfProductsInFridge(mNumberOfProducts);

        ExpiringProductsReminderUtilities.scheduleExpiringProductsReminder(this);

        todayDate = ProductDateUtilities.todayDateInMillis();

        getSupportLoaderManager().initLoader(ID_DATES_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case (R.id.action_about):
                Intent openAboutActivityIntent= new Intent(MainActivity.this, AboutActivity.class);
                startActivity(openAboutActivityIntent);
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    public void addProductActivity(View view) {
        Intent addProductIntent = new Intent(this, AddProductActivity.class);
        startActivity(addProductIntent);
    }

    public void openFridge(View view) {
        Intent productsListIntent = new Intent(this, ProductsActivity.class);
        startActivity(productsListIntent);
    }

    public void openShoppingList(View view){
        Intent addProductListIntent= new Intent(this, ProductsListActivity.class);
        startActivity(addProductListIntent);
    }

    private Cursor getAllProducts() {
        return mDb.query(ProductsContract.ProductsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ProductsContract.ProductsEntry.COLUMN_EXPIRY_DATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numberOfProducts = getAllProducts().getCount();
        showNumberOfProductsInFridge(numberOfProducts);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case ID_DATES_LOADER:
                /* Uri for all rows of product data */
                Uri productQueryUri = ProductsContract.ProductsEntry.PRODUCTS_CONTENT_URI;
                // whereClause and selectionArgs are for only querying those goods with the expiry
                // date same as the current date.
                String whereClause = ProductsContract.ProductsEntry.COLUMN_EXPIRY_DATE + " <? ";
                String[] selectionArgs = new String[]{String.valueOf(todayDate)};
                return new CursorLoader(this,
                        productQueryUri,
                        null,
                        whereClause,
                        selectionArgs,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //If data returned is not null, it means that there are products expiring the current day.
        // So show it in a TextView in the activity_main.xml
        try{
            int rowsReturned= data.getCount();
            if (rowsReturned > 0) {
                String productsExpiringString = String.valueOf(rowsReturned);
                final TextView mExpiringProductsTextView = findViewById(R.id.tv_expiring_products);
                if (rowsReturned == 1) {
                    mExpiringProductsTextView.setText("You have " + productsExpiringString +
                            " product expired/expiring!");
                } else {
                    mExpiringProductsTextView.setText("You have " + productsExpiringString +
                            " products expired/expiring!");
                }

            }

        }
        catch (NullPointerException npe) {
            Log.v(TAG, "Did not find any expiring products...");
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showNumberOfProductsInFridge(int numberOfProducts) {
        if (numberOfProducts > 1) {
            mFridgeContentTextView.setText("You have " + numberOfProducts + " products in your fridge");
        } else if (numberOfProducts == 1) {
            mFridgeContentTextView.setText("You have " + numberOfProducts + " product in your fridge");
        } else {
            mFridgeContentTextView.setText(R.string.empty_fridge);
        }
    }

}
