package com.georgeampartzidis.greenfridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class ProductsListActivity extends AppCompatActivity implements OnClickListener{
    public static final int COLUMN_PRODUCT_ID = 0;
    public static final int COLUMN_PRODUCT_NAME = 1;
    private static final String LOG_TAG = ProductsListActivity.class.getSimpleName();
    public static final String[] MAIN_PRODUCT_PROJECTION = new String[]{"_id", "productName"};
    private static final int PRODUCTS_LIST_LOADER = 33;
    private long id;
    private SQLiteDatabase mDb;
    private int mPosition = -1;
    private ProductsListAdapter mProductsListAdapter;


    public void onClick(View view) {
            ProductsListActivity.this.startActivity(new Intent(ProductsListActivity.this, MainActivity.class));
        }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_products_list);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview_products);
        this.mDb = new ProductsDbHelper(this).getWritableDatabase();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.mProductsListAdapter = new ProductsListAdapter(this, getAllProducts());
        mRecyclerView.setAdapter(this.mProductsListAdapter);
        new ItemTouchHelper(new SimpleCallback(0, 12) {

            /* renamed from: com.georgeampartzidis.greenfridge.ProductsListActivity$2$2 */
            class C03292 implements DialogInterface.OnClickListener {
                C03292() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    ProductsListActivity.this.removeProductFromList(ProductsListActivity.this.id);
                    ProductsListActivity.this.mProductsListAdapter.swapCursor(ProductsListActivity.this.getAllProducts());
                }
            }

            /* renamed from: com.georgeampartzidis.greenfridge.ProductsListActivity$2$3 */
            class C03303 implements DialogInterface.OnClickListener {
                C03303() {
                }

                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                    ProductsListActivity.this.mProductsListAdapter.swapCursor(ProductsListActivity.this.getAllProducts());
                }
            }

            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                return false;
            }

            public void onSwiped(final ViewHolder viewHolder, int direction) {
                ProductsListActivity.this.id = (Long) viewHolder.itemView.getTag();
                Builder builder = new Builder(new ContextThemeWrapper(ProductsListActivity.this, R.style.CustomAlertDialog));
                builder.setTitle((int) R.string.alert_dialog_title).setMessage((int) R.string.alert_dialog_list_message);
                builder.setNeutralButton((int) R.string.delete_and_add_in_fridge, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String productString = ((TextView) viewHolder.itemView.findViewById(R.id.tv_product)).getText().toString();
                        Intent addProductActivity = new Intent(ProductsListActivity.this, AddProductActivity.class);
                        addProductActivity.putExtra("FROM_ACTIVITY", ProductsListActivity.LOG_TAG);
                        addProductActivity.putExtra("PUT_TO_FRIDGE", productString);
                        ProductsListActivity.this.startActivity(addProductActivity);
                        ProductsListActivity.this.removeProductFromList(ProductsListActivity.this.id);
                        ProductsListActivity.this.mProductsListAdapter.swapCursor(ProductsListActivity.this.getAllProducts());
                    }
                });
                builder.setNegativeButton((int) R.string.delete, new C03292());
                builder.setPositiveButton((int) R.string.cancel, new C03303());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (ProductsListActivity.this.getScreenWidth() < 600 && ProductsListActivity.this.getRequestedOrientation() == 1) {
                    alertDialog.getWindow().setLayout(-1, -2);
                }
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    protected void onResume() {
        super.onResume();
        this.mProductsListAdapter.swapCursor(getAllProducts());
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
