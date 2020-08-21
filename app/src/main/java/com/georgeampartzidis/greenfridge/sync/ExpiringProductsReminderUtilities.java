package com.georgeampartzidis.greenfridge.sync;

import android.content.Context;
import androidx.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by georgeampartzidis on 17/02/2018.
 */

public class ExpiringProductsReminderUtilities {

    public static final String LOG_TAG = ExpiringProductsReminderUtilities.class.getSimpleName();

    public static final int REMINDER_INTERVAL_SECONDS = 0;
    public static final int SYNC_FLEXTIME_SECONDS = 10;
    public static final String REMINDER_JOB_TAG = "expiring-products-reminder-tag";
    private static boolean isInitialized;

    public static void scheduleExpiringProductsReminder(@NonNull final Context context) {
        if (isInitialized) {
            return;
        }
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job expiryReminderJob = dispatcher.newJobBuilder()
                .setService(ExpiringProductsReminderFireJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS, SYNC_FLEXTIME_SECONDS))
                .build();

        dispatcher.schedule(expiryReminderJob);
        isInitialized = true;
    }
}
