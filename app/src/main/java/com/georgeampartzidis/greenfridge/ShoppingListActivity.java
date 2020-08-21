package com.georgeampartzidis.greenfridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;

public class ShoppingListActivity extends AppCompatActivity implements OnClickListener{
    public static final int COLUMN_PRODUCT_ID = 0;
    public static final int COLUMN_PRODUCT_NAME = 1;
    private static final String LOG_TAG = ShoppingListActivity.class.getSimpleName();
    public static final String[] MAIN_PRODUCT_PROJECTION = new String[]{"_id", "productName"};
    private static final int PRODUCTS_LIST_LOADER = 33;
    private long id;
    private SQLiteDatabase mDb;
    private int mPosition = -1;
    private ShoppingListAdapter mShoppingListAdapter;


    public void onClick(View view) {
            ShoppingListActivity.this.startActivity(new Intent(ShoppingListActivity.this, MainActivity.class));
        }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_shopping_list);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview_products);
        this.mDb = new ProductsDbHelper(this).getWritableDatabase();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productsActivityIntent = new Intent(ShoppingListActivity.this, ProductsActivity.class);
                startActivity(productsActivityIntent);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.mShoppingListAdapter = new ShoppingListAdapter(this, getAllProducts());
        mRecyclerView.setAdapter(this.mShoppingListAdapter);
        new ItemTouchHelper(new SimpleCallback(0, 12) {

            /* renamed from: com.georgeampartzidis.greenfridge.ShoppingListActivity$2$2 */
            class C03292 implements DialogInterface.OnClickListener {
                C03292() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    ShoppingListActivity.this.removeProductFromList(ShoppingListActivity.this.id);
                    ShoppingListActivity.this.mShoppingListAdapter.swapCursor(ShoppingListActivity.this.getAllProducts());
                }
            }

            /* renamed from: com.georgeampartzidis.greenfridge.ShoppingListActivity$2$3 */
            class C03303 implements DialogInterface.OnClickListener {
                C03303() {
                }

                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                    ShoppingListActivity.this.mShoppingListAdapter.swapCursor(ShoppingListActivity.this.getAllProducts());
                }
            }

            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                return false;
            }

            public void onSwiped(final ViewHolder viewHolder, int direction) {
                ShoppingListActivity.this.id = (Long) viewHolder.itemView.getTag();
                Builder builder = new Builder(new ContextThemeWrapper(ShoppingListActivity.this, R.style.CustomAlertDialog));
                builder.setTitle((int) R.string.alert_dialog_title).setMessage((int) R.string.alert_dialog_list_message);
                builder.setNeutralButton((int) R.string.delete_and_add_in_fridge, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String productString = ((TextView) viewHolder.itemView.findViewById(R.id.tv_product)).getText().toString();
                        Intent addProductActivity = new Intent(ShoppingListActivity.this, AddProductActivity.class);
                        addProductActivity.putExtra("FROM_ACTIVITY", ShoppingListActivity.LOG_TAG);
                        addProductActivity.putExtra("PUT_TO_FRIDGE", productString);
                        ShoppingListActivity.this.startActivity(addProductActivity);
                        ShoppingListActivity.this.removeProductFromList(ShoppingListActivity.this.id);
                        ShoppingListActivity.this.mShoppingListAdapter.swapCursor(ShoppingListActivity.this.getAllProducts());
                    }
                });
                builder.setNegativeButton((int) R.string.delete, new C03292());
                builder.setPositiveButton((int) R.string.cancel, new C03303());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (ShoppingListActivity.this.getScreenWidth() < 600 && ShoppingListActivity.this.getRequestedOrientation() == 1) {
                    alertDialog.getWindow().setLayout(-1, -2);
                }
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    protected void onResume() {
        super.onResume();
        this.mShoppingListAdapter.swapCursor(getAllProducts());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShoppingListActivity.this, ProductsActivity.class));
        finish();
    }

    private Cursor getAllProducts() {
        return this.mDb.query("productsList", null, null, null, null, null, "_id");
    }

    private boolean removeProductFromList(long id) {
        return this.mDb.delete("productsList", new StringBuilder().append("_id=").append(id).toString(), null) > 0;
    }

    public void addToShoppingList(View view) {
        startActivity(new Intent(this, AddProductToListActivity.class));
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
