package com.georgeampartzidis.greenfridge.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by georgeampartzidis on 27/01/2018.
 */

public class ProductsContract {

    public static final String CONTENT_AUTHORITY = "com.georgeampartzidis.greenfridge";
    public static final String SCHEMA = "content://";

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "product";
    public static final String PATH_PRODUCTS_LIST = "productsList";

    public static final class ProductsEntry implements BaseColumns {
        /* This is the base CONTENT_URI used to query the Product table from the content provider */
        public static final Uri PRODUCTS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PRODUCT)
                .build();

        public static final String TABLE_NAME = "product";
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_EXPIRY_DATE = "expiryDate";
    }

    public static final class ProductsListEntry implements BaseColumns {
        /* base CONTENT_URI used to query the productsList table from the content provider */
        public static final Uri PRODUCTS_LIST_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PRODUCTS_LIST)
                .build();

        public static final String TABLE_NAME = "productsList";
        public static final String COLUMN_PRODUCT_NAME = "productName";
    }
}
