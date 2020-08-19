package com.georgeampartzidis.greenfridge;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.georgeampartzidis.greenfridge.data.ProductsContract;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;

public class ProductsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /* The columns of the data we are interested in displaying within our Products Activity's list */
    public static final String[] MAIN_PRODUCT_PROJECTION = {
            ProductsContract.ProductsEntry._ID,
            ProductsContract.ProductsEntry.COLUMN_PRODUCT_NAME,
            ProductsContract.ProductsEntry.COLUMN_EXPIRY_DATE
    };

    public static final int INDEX_PRODUCT_ID = 0;
    public static final int INDEX_PRODUCT_NAME = 1;
    public static final int INDEX_PRODUCT_EXPIRY_DATE = 2;

    private final String LOG_TAG = ProductsActivity.class.getSimpleName();
    private static final int ID_PRODUCTS_LOADER = 22;

    RecyclerView mRecyclerView;
    FloatingActionButton mFloatingActionButton;
    private ProductsAdapter mProductAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private SQLiteDatabase mDb;
    private long id;
    private Toolbar mToolbar;
    private TextView mEmptyFridgeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        ProductsDbHelper dbHelper = new ProductsDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mRecyclerView = findViewById(R.id.recyclerview_products);
        mFloatingActionButton = findViewById(R.id.bt_add_product);
        mToolbar = findViewById(R.id.toolbar);
        mEmptyFridgeTextView= findViewById(R.id.tv_empty_fridge);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivityIntent = new Intent(ProductsActivity.this,
                        MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });


          /*
           * A LinearLayoutManager is responsible for measuring and positioning item views within a
           * RecyclerView into a linear list.
           * */

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        Cursor cursor = getAllProducts();
        mProductAdapter = new ProductsAdapter(this, cursor);
        mRecyclerView.setAdapter(mProductAdapter);


        getSupportLoaderManager().initLoader(ID_PRODUCTS_LOADER, null, this);


         /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                id = (long) viewHolder.itemView.getTag();
                AlertDialog alertDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ProductsActivity.this,
                        R.style.CustomAlertDialog));
                builder.setTitle(R.string.alert_dialog_title)
                        .setMessage(R.string.alert_dialog_message);

                /**
                 * The names of the buttons (neutral, negative etc) don't reflect their actions,
                 * rather that used to place each function accordingly
                 */
                builder.setNeutralButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                removeProduct(id);
                                // When removing a product, if it had expired, its view color would be red, so we
                                // don't want it to be reused because it may be used for a non- expired program
                                viewHolder.setIsRecyclable(false);
                                mProductAdapter.swapCursor(getAllProducts());
                            }
                        });

                builder.setNegativeButton(R.string.delete_and_add_in_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView productTv = viewHolder.itemView.findViewById(R.id.tv_product);
                        String product = productTv.getText().toString();
                        addProductToList(product);
                        removeProduct(id);
                        viewHolder.setIsRecyclable(false);
                        mProductAdapter.swapCursor(getAllProducts());
                    }
                });

                builder.setPositiveButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                                mProductAdapter.swapCursor(getAllProducts());
                            }
                        });
                alertDialog = builder.create();
                alertDialog.show();

                /**
                 * Check the screen width and set the alertDialog window accordingly- if the width
                 * is small then the Dialog width must take the whole width space for the button
                 * titles to be shown properly
                 */
                int screenWidth = getScreenWidth();
                if (screenWidth < 600) {
                    if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                }
            }
        }).attachToRecyclerView(mRecyclerView);


        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                }
            }
        };

        mRecyclerView.addOnScrollListener(onScrollListener);

    }


    /**
     * Called when a new Loader needs to be created. This Activity only uses one loader, however
     * it is a good practice
     *
     * @param loaderId
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case ID_PRODUCTS_LOADER:
                /* Uri for all rows of product data */
                Uri productQueryUri = ProductsContract.ProductsEntry.PRODUCTS_CONTENT_URI;
                String sortOrder = ProductsContract.ProductsEntry.COLUMN_EXPIRY_DATE;

                return new CursorLoader(this,
                        productQueryUri,
                        MAIN_PRODUCT_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data!= null){
            mEmptyFridgeTextView.setVisibility(View.INVISIBLE);
        }
        mProductAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);
    }


    private boolean removeProduct(long id) {
        return mDb.delete(ProductsContract.ProductsEntry.TABLE_NAME,
                ProductsContract.ProductsEntry._ID + "=" + id,
                null) > 0;

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

    // This is called when returning to the Products Activity (e.g. after adding a product)
    @Override
    protected void onResume() {
        super.onResume();
        mProductAdapter.swapCursor(getAllProducts());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProductsActivity.this, MainActivity.class));
        finish();
    }

    /*
    * This method is called to handle the problem of the Up button not working when rotating the
    * device.
    * */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent MainActivityIntent = new Intent(ProductsActivity.this,
                            MainActivity.class);
                    startActivity(MainActivityIntent);
                }
            });
        }
    }

    public void addProductActivity(View view) {
        Intent addProductIntent = new Intent(this, AddProductActivity.class);
        startActivity(addProductIntent);
    }

    public long addProductToList(String product) {
        Log.v(LOG_TAG, product + " will be added to the list");
        ContentValues cv = new ContentValues();
        cv.put(ProductsContract.ProductsListEntry.COLUMN_PRODUCT_NAME, product);
        long rowInserted = mDb.insert(ProductsContract.ProductsListEntry.TABLE_NAME, null, cv);

        if (rowInserted != -1) {
            Toast.makeText(getApplicationContext(), product + " was added to your list", Toast.LENGTH_SHORT).show();
        }
        return rowInserted;
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }
}
