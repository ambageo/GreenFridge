package com.georgeampartzidis.greenfridge;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductsListAdapter extends Adapter<ProductsListAdapter.ListViewHolder> {
    private final Context mContext;
    private Cursor mCursor;

    public class ListViewHolder extends ViewHolder {
        TextView productView;

        public ListViewHolder(View itemView) {
            super(itemView);
            this.productView = (TextView) itemView.findViewById(R.id.tv_product);
        }
    }

    public ProductsListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.product_shopping_list_item, viewGroup, false);
        view.setFocusable(true);
        return new ListViewHolder(view);
    }

    public void onBindViewHolder(ListViewHolder viewHolder, int position) {
        int count = getItemCount();
        Log.d("ggg", "cursor has " + count + " elements");
        if(!mCursor.moveToPosition(position)){
            return;
        }
            long id = mCursor.getLong(ProductsListActivity.COLUMN_PRODUCT_ID);
            viewHolder.productView.setText(mCursor.getString(ProductsListActivity.COLUMN_PRODUCT_NAME));
            viewHolder.itemView.setTag(id);
    }

    public int getItemCount() {
        if (this.mCursor == null) {
            return 0;
        }
        return this.mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        if (this.mCursor != null) {
            this.mCursor.close();
        }
        this.mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }
}