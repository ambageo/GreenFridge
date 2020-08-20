package com.georgeampartzidis.greenfridge;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;

public class AddProductToListActivity extends AppCompatActivity implements OnClickListener, DialogInterface.OnClickListener {
    private SQLiteDatabase mDb;
    private EditText mProductEditText;
    private Toolbar mToolbar;

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }


        @Override
        public void onClick(View view) {
            AddProductToListActivity.this.startActivity(new Intent(AddProductToListActivity.this, ShoppingListActivity.class));
        }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_add_product_to_list);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.mProductEditText = (EditText) findViewById(R.id.product);
        this.mDb = new ProductsDbHelper(this).getWritableDatabase();
    }

    public void addToList(View view) {
        if (this.mProductEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter a product", 0).show();
        }
        addProduct(this.mProductEditText.getText().toString());
        this.mProductEditText.getText().clear();
    }

    private long addProduct(String product) {
        ContentValues cv = new ContentValues();
        cv.put("productName", product);
        long rowInserted = this.mDb.insert("productsList", null, cv);
        if (rowInserted != -1) {
            Toast.makeText(this, product + " successfully added to the shopping list!", 0).show();
        }
        return rowInserted;
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ShoppingListActivity.class));
        finish();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            this.mToolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}