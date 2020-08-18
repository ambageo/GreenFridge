package com.georgeampartzidis.greenfridge.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.content.ContextCompat;

import com.georgeampartzidis.greenfridge.ProductsActivity;
import com.georgeampartzidis.greenfridge.R;
import com.georgeampartzidis.greenfridge.sync.ProductsReminderIntentService;
import com.georgeampartzidis.greenfridge.sync.ReminderTasks;

import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;


/**
 * Created by georgeampartzidis on 13/02/2018.
 */

public class NotificationUtilities {
    public final static int ACTION_OPEN_PENDING_INTENT_ID = 1;
    public static final int ACTION_IGNORE_PENDING_INTENT = 15;
    public final static String NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";
    public static final int NOTIFICATION_REMINDER_ID = 23;

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void RemindUser(Context context, int expiringProducts) {

        String contentTextString;
        if (expiringProducts > 1) {
            contentTextString= context.getString(R.string.many_expiring_products, expiringProducts);
        } else {
            contentTextString = context.getString(R.string.one_expiring_product);
        }
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O devices. Since Oreo notifications
        // must belong to a Notification Channel, otherwise they're not displayed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.background))
                .setSmallIcon(R.drawable.ic_fridge_notification)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(contentTextString)
                .setDefaults(DEFAULT_VIBRATE)
                .addAction(openProductsActivityAction(context))
                .addAction(ignoreReminderAction(context))
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);


        // If the build version is greater than JELLY_BEAN and lower than OREO,
        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(NOTIFICATION_REMINDER_ID, notificationBuilder.build());
    }

    private static Action ignoreReminderAction(Context context) {
        Intent ignoreReminderIntent = new Intent(context, ProductsReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Action ignoreReminderAction = new Action(R.drawable.ic_cancel, "Ok, dismiss",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    public static Action openProductsActivityAction(Context context) {
        Intent openProductsActivityIntent = new Intent(context, ProductsActivity.class);
        PendingIntent openProductsActivityPendingIntent = PendingIntent.getActivity(
                context,
                ACTION_OPEN_PENDING_INTENT_ID,
                openProductsActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Action openProductsActivityAction = new Action(R.drawable.ic_fridge_notification,
                "open fridge",
                openProductsActivityPendingIntent);
        return openProductsActivityAction;
    }


    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, ProductsActivity.class);
        return PendingIntent.getActivity(context,
                ACTION_OPEN_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
