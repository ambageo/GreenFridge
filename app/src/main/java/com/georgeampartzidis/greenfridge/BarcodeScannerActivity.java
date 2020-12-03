package com.georgeampartzidis.greenfridge;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.georgeampartzidis.greenfridge.utilities.NetworkUtilities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import loaders.BarcodeLoader;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BarcodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, LoaderManager.LoaderCallbacks<String> {

    private final String TAG = BarcodeScannerActivity.class.getName();

    private static final int REQUEST_CAMERA = 2;
    private static final int BARCODE_LOADER = 11;
    private static final String QUERY = "query_string";

    private String originActivity = "";

    private ZXingScannerView scannerView;

    private Intent addProductIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null){
            originActivity = intent.getExtras().getString("FROM_ACTIVITY");
            Log.d("ggg", " Barcode scanner from activity: "+ originActivity);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(originActivity.equals("AddProductToListActivity")){
                    addProductIntent= new Intent(BarcodeScannerActivity.this, AddProductToListActivity.class);
                    startActivity(addProductIntent);
                } else {
                    addProductIntent= new Intent(BarcodeScannerActivity.this, AddProductActivity.class);
                    startActivity(addProductIntent);
                }

            }
        });



        ViewGroup contentFrame = findViewById(R.id.content_frame);
        scannerView = new ZXingScannerView(this);
        contentFrame.addView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( !checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[] {CAMERA}, REQUEST_CAMERA);
    }



    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        Log.d(TAG, "started camera");
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
        Log.d(TAG, "stopped camera");
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.d(TAG, rawResult.getText());

        String barCodeQuery = NetworkUtilities.buildQuery(rawResult.getText());
        Bundle bundle = new Bundle();
        bundle.putString(QUERY, barCodeQuery);
        startBarcodeLoader(bundle);
    }

    private void startBarcodeLoader(Bundle queryBundle) {
        LoaderManager.getInstance(this).initLoader(BARCODE_LOADER, queryBundle, this);

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int loaderId, @Nullable Bundle args) {

        String queryString = "";
        if(args!= null) {
            queryString = args.getString(QUERY);
        }

        if(loaderId == BARCODE_LOADER){
            return new BarcodeLoader(this, queryString);
        } else {
            return null;
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.optString("status_verbose");
            if (status.equalsIgnoreCase("product_found") || status.equalsIgnoreCase("product found")){
                JSONObject productObject = jsonObject.optJSONObject("product");
                String productBrand = productObject.optString("brands");
                String productName = productObject.optString("product_name");
                JSONObject imagesObject = productObject.optJSONObject("selected_images");
                JSONObject frontImagesObject = imagesObject.optJSONObject("front");
                String productImageUrl = frontImagesObject.optString("thumb");
                Log.d(TAG, productBrand + " " + productName + " " + productImageUrl);

                Intent intent = new Intent();
                intent.putExtra("scanned_product", productBrand + " " + productName);
                setResult(RESULT_OK, intent);
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
            }
            finish();

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
