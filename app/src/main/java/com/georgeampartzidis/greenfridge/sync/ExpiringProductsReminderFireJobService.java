package com.georgeampartzidis.greenfridge.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.georgeampartzidis.greenfridge.MainActivity;
import com.georgeampartzidis.greenfridge.data.ProductsContract;
import com.georgeampartzidis.greenfridge.data.ProductsDbHelper;
import com.georgeampartzidis.greenfridge.utilities.NotificationUtilities;
import com.georgeampartzidis.greenfridge.utilities.ProductDateUtilities;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by georgeampartzidis on 17/02/2018.
 */

public class ExpiringProductsReminderFireJobService extends JobService {
    private final String LOG_TAG= ExpiringProductsReminderUtilities.class.getSimpleName();
    private AsyncTask mBackgroundTask;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask= new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context  context = ExpiringProductsReminderFireJobService.this;
                ProductsDbHelper dbHelper= new ProductsDbHelper(context);
                mDb= dbHelper.getReadableDatabase();
                long todayDateinMillis= ProductDateUtilities.todayDateInMillis();
                String whereClause= ProductsContract.ProductsEntry.COLUMN_EXPIRY_DATE + " <? ";
                String[] selectionArgs= new String[]{String.valueOf(todayDateinMillis)};
                mCursor=mDb.query(ProductsContract.ProductsEntry.TABLE_NAME,
                        null,
                        whereClause,
                        selectionArgs,
                        null,
                        null,
                        ProductsContract.ProductsEntry.COLUMN_EXPIRY_DATE);
                int rowsReturned= mCursor.getCount();
Log.v(LOG_TAG, "Number of products expired: " + String.valueOf(rowsReturned));
                if( rowsReturned> 0){
                    NotificationUtilities.RemindUser(context, rowsReturned);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
if(mBackgroundTask != null){
    mBackgroundTask.cancel(true);
}
        return true;
    }
}
