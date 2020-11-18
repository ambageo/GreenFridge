package com.georgeampartzidis.greenfridge;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.georgeampartzidis.greenfridge.data.ProductsContract.ProductsEntry;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;
import com.georgeampartzidis.greenfridge.utilities.ProductDateUtilities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class
AddProductActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private final static String LOG_TAG = AddProductActivity.class.getSimpleName();

    private final static int REQUEST_CODE = 3;
    private EditText productEditText;
    private EditText dateEditText;
    private SQLiteDatabase mDb;
    private Toolbar mToolbar;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Button functionality of the toolbar- when pressed it launches ProductActivity
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProductIntent = new Intent(AddProductActivity.this,
                        ProductsActivity.class);
                startActivity(addProductIntent);
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        productEditText = findViewById(R.id.product);
        dateEditText = findViewById(R.id.date);

        // Get the intent
        Intent intent = this.getIntent();
        /**
         * Get the intent's extras. If there are no extras, it means that the activity is launched
         * from the Main Activity. If there are extras, it is launched from the ProductsList Activity,
         * so get the product string and set it to the TextView
         */
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String product = intent.getExtras().getString("PUT_TO_FRIDGE");
            productEditText.setText(product);
        }

        ProductsDbHelper dbHelper = new ProductsDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        //final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        final DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        dateEditText.setText(dateFormat.format(cal.getTime()));
    }

    public void addToFridge(View view) {
        if (productEditText.getText().length() == 0 || dateEditText.getText().length() == 0) {
            Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String productString = productEditText.getText().toString();
        String expiryDateString = dateEditText.getText().toString();
        long expiryDateInMillis = ProductDateUtilities.convertDateStringToMillis(expiryDateString);


        addProduct(productString, expiryDateInMillis);

        productEditText.getText().clear();
        dateEditText.getText().clear();
    }

    private long addProduct(String product, long expiryDate) {
        // Create a ContentValues instance and pass the values for the insert query
        ContentValues cv = new ContentValues();
        cv.put(ProductsEntry.COLUMN_PRODUCT_NAME, product);
        cv.put(ProductsEntry.COLUMN_EXPIRY_DATE, expiryDate);
        long rowInserted = mDb.insert(ProductsEntry.TABLE_NAME, null, cv);

        if (rowInserted != -1) {
            Toast.makeText(this, product + " successfully added to the fridge!"
                    , Toast.LENGTH_SHORT).show();
        }

        return rowInserted;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddProductActivity.this, ProductsActivity.class));
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent MainActivityIntent = new Intent(AddProductActivity.this,
                            ProductsActivity.class);
                    startActivity(MainActivityIntent);
                }
            });
        }
    }

    public void scanProduct(View view) {
        Intent scanIntent = new Intent(AddProductActivity.this, BarcodeScannerActivity.class);
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
