package com.georgeampartzidis.greenfridge;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AddProductToListActivity extends AppCompatActivity implements OnClickListener, DialogInterface.OnClickListener {

    private final static String LOG_TAG = AddProductToListActivity.class.getSimpleName();

    private final static int REQUEST_CODE = 4;

    private SQLiteDatabase mDb;
    private EditText productEditText;
    private Toolbar toolbar;
    private AdView adView;

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }


    @Override
    public void onClick(View view) {
        AddProductToListActivity.this.startActivity(new Intent(AddProductToListActivity.this, ShoppingListActivity.class));
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shoppingListActivityIntent = new Intent(AddProductToListActivity.this, ShoppingListActivity.class);
                startActivity(shoppingListActivityIntent);
            }
        });
        this.productEditText = (EditText) findViewById(R.id.product);
        this.mDb = new ProductsDbHelper(this).getWritableDatabase();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void addToList(View view) {
        if (this.productEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter a product", Toast.LENGTH_SHORT).show();
            return;
        }
        addProduct(this.productEditText.getText().toString());
        this.productEditText.getText().clear();
    }

    private long addProduct(String product) {
        ContentValues cv = new ContentValues();
        cv.put("productName", product);
        long rowInserted = this.mDb.insert("productsList", null, cv);
        if (rowInserted != -1) {
            Toast.makeText(this, product + " successfully added to the shopping list!", Toast.LENGTH_SHORT).show();
        }
        return rowInserted;
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddProductToListActivity.this, ShoppingListActivity.class));
        finish();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            this.toolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    public void scanProduct(View view) {
        Intent scanIntent = new Intent(AddProductToListActivity.this, BarcodeScannerActivity.class);
        scanIntent.putExtra("FROM_ACTIVITY", LOG_TAG);
        startActivityForResult(scanIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String scannedProduct = data.getStringExtra("scanned_product");
                productEditText.setText(scannedProduct);
            } else {
                productEditText.setText("");
                Toast.makeText(this, "Product not found, please fill in the fields or try scanning a different product", Toast.LENGTH_LONG).show();
            }
        }
    }
}