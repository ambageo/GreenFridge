package com.georgeampartzidis.greenfridge;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by georgeampartzidis on 28/01/2018.
 */

public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar= Calendar.getInstance();
        int year= calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        int day= calendar.get(Calendar.DAY_OF_MONTH);

          int theme;
          if(Build.VERSION.SDK_INT<23){
              theme= AlertDialog.THEME_HOLO_DARK;
          } else {
              theme= android.R.style.Theme_Holo_Dialog;
          }

        DatePickerDialog datePickerDialog= new DatePickerDialog(getActivity(),
                theme,
                (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);

          // setMinDate makes sure that the user cannot pick up a past date
        DatePicker datePicker =datePickerDialog.getDatePicker();
        // workaround for the problem that the minDate can't be exactly equal to the current Date,
        // so I subtract a second from the current Date
        datePicker.setMinDate(System.currentTimeMillis()- 1000);

        // Because of a bug, the Calendar View in tablets shows November 2100 as the starting Date,
        // so I disable it.
        datePicker.setCalendarViewShown(false);
        return datePickerDialog;
    }


}
