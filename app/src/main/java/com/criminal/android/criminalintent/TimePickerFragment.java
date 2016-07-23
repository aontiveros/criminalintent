package com.criminal.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by anton on 7/20/2016.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String ARG_TIME = "com.ciminalintent.android.TimePickerFragment.time";

    private static TimePicker mTimePickerDialog;

    public static TimePickerFragment newInstance(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TIME, date);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date time = (Date) getArguments().getSerializable(ARG_TIME);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePickerDialog = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        mTimePickerDialog.setCurrentHour(calendar.get(Calendar.HOUR));
        mTimePickerDialog.setCurrentMinute(calendar.get(Calendar.MINUTE));



        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour = mTimePickerDialog.getCurrentHour();
                        int minute = mTimePickerDialog.getCurrentMinute();
                        Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .setView(v)
                .create();
    }

    public void sendResult(int resultCode, Date date){
        if(date == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(ARG_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
