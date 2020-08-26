package loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.georgeampartzidis.greenfridge.utilities.NetworkUtilities;

public class BarcodeLoader extends AsyncTaskLoader<String> {

    private String queryString;
    public BarcodeLoader(@NonNull Context context, String queryString) {
        super(context);
        this.queryString = queryString;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtilities.getJSONResults(queryString);
    }
}
