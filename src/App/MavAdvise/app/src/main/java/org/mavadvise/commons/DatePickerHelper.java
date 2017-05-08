package org.mavadvise.commons;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import org.mavadvise.R;

/**
 * Created by SaiKumar on 4/10/2017.
 */

public class DatePickerHelper extends DialogFragment {

    public interface DatePickerListener {
        public void onDatePickerFinish(Calendar date);
    }

    private DatePickerListener datePickerListener;
    private Calendar minDate = Calendar.getInstance();
    private Calendar maxDate = null;

    public DatePickerHelper() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datepicker, container, false);
        getDialog().setTitle("Select Date");

        final DatePicker dp = (DatePicker) rootView.findViewById(R.id.datePicker);
        dp.setMinDate(minDate.getTimeInMillis());

        if (maxDate != null) {
            dp.setMaxDate(maxDate.getTimeInMillis());
        }

        Button saveButton = (Button) rootView.findViewById(R.id.dpSelectBT);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                onDatePickerFinish(selectedTime);
            }
        });
        Button cancelButton = (Button) rootView.findViewById(R.id.dpCancelBT);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return rootView;
    }

    private void onDatePickerFinish(Calendar date) {
        datePickerListener.onDatePickerFinish(date);
        getDialog().dismiss();
    }

    public void setOnSumbitListener(DatePickerListener datePickerListener) {
        this.datePickerListener = datePickerListener;
    }

    public void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(Calendar maxDate) {
        this.maxDate = maxDate;
    }
}
