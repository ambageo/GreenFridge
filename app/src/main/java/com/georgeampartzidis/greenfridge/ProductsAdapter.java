package com.georgeampartzidis.greenfridge;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.georgeampartzidis.greenfridge.utilities.ProductDateUtilities;

/**
 * Created by georgeampartzidis on 02/02/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.NumberViewHolder> {

    private static final String LOG_TAG = ProductsAdapter.class.getSimpleName();
    private final Context mContext;
    private Cursor mCursor;
    private static final long DAY_IN_MILLISECONDS = 86400000;

    public ProductsAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId = R.layout.product_list_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NumberViewHolder viewHolder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        String productName = mCursor.getString(ProductsActivity.INDEX_PRODUCT_NAME);
        long expiryDate = mCursor.getLong(ProductsActivity.INDEX_PRODUCT_EXPIRY_DATE);
        String expiryDateString = ProductDateUtilities.convertMillisToDateString(expiryDate);
        // Also retrieve the id from the cursor to pass it in the itemView
        long id = mCursor.getLong(ProductsActivity.INDEX_PRODUCT_ID);
        viewHolder.productView.setText(productName);
        viewHolder.dateView.setText(expiryDateString);

        long currentDate = ProductDateUtilities.todayDateInMillis();

        if (expiryDate < currentDate) {
            if ((currentDate - expiryDate) > DAY_IN_MILLISECONDS) {
                viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.expired));
                viewHolder.dateView.setText(mContext.getResources().getString(R.string.expired_string));
            } else {
                viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.expiring));
                viewHolder.dateView.setText(mContext.getResources().getString(R.string.today));
            }
            viewHolder.dateView.setTextSize(20);
        } else {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));
        }
        //Set the id so that it can be accessed with itemView.getTag()
        viewHolder.itemView.setTag(id);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our products table
     */
    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();

    }

    /**
     * Swaps the cursor used by the ProductsAdapter for its product data. This method is called by
     * ProductsActivity after a load has finished, as well as when the Loader responsible for loading
     * the product data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ProductsAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        if (mCursor != null)
            mCursor.close();
        mCursor = newCursor;
        if (newCursor != null)
            this.notifyDataSetChanged();
    }

    class NumberViewHolder extends RecyclerView.ViewHolder {

        TextView productView;
        TextView dateView;

        public NumberViewHolder(View itemView) {
            super(itemView);
            productView = itemView.findViewById(R.id.tv_product);
            dateView = itemView.findViewById(R.id.tv_expiry_date);
        }
    }
}
