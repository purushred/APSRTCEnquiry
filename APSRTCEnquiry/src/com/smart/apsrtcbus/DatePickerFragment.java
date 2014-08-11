package com.smart.apsrtcbus;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
 {

	private DatePickerDialog.OnDateSetListener onDateSetListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar cal = (Calendar) getArguments().getSerializable("Calendar");
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
	}

	public void setOnDateSetListener(
			DatePickerDialog.OnDateSetListener onDateSetListener) {
		this.onDateSetListener = onDateSetListener;
	}
}