package com.georgeampartzidis.greenfridge.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by georgeampartzidis on 17/02/2018.
 */

public class ProductsReminderIntentService extends IntentService {
    public ProductsReminderIntentService(){
        super("ProductsReminderIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String action= intent.getAction();
        ReminderTasks.executeTask(this, action);
    }
}
