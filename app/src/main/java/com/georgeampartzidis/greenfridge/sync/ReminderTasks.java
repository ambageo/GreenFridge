package com.georgeampartzidis.greenfridge.sync;

import android.content.Context;

import com.georgeampartzidis.greenfridge.utilities.NotificationUtilities;

/**
 * Created by georgeampartzidis on 17/02/2018.
 */

public class ReminderTasks {
    public static final String ACTION_SHOW_PRODUCTS= "show-products";
    public static final String ACTION_DISMISS_NOTIFICATION= "dismiss-notification";

    public static void executeTask(Context context, String action){
        if(ACTION_SHOW_PRODUCTS.equals(action)){
NotificationUtilities.clearNotifications(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)){
            NotificationUtilities.clearNotifications(context);
        }
    }
}
