package com.smart.apsrtcbus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.apsrtcbus.utilities.AppUtils;
import com.smart.apsrtcbus.vo.SearchResultVO;
import com.smart.apsrtcbus.vo.ServiceInfo;

public class MainActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener{

	private RequestQueue requestQueue = null;

	private EditText fromTextView;
	private EditText toTextView;
	private Button journeyDateButton;
	private AdView adView;

	private ServiceInfo fromServiceInfo = null;
	private ServiceInfo toServiceInfo = null;
	private String journeyDateStr = null;

	private ProgressDialog progress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(savedInstanceState!=null)
			journeyDateStr = savedInstanceState.getString("journeyDate");
		if(!AppUtils.isNetworkOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
		{
			buildNetworkErrorUI();
		}
		else
		{

			displayMainScreen();
		}
	}

	private void buildNetworkErrorUI() {

		LinearLayout layout = new LinearLayout(this);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.VERTICAL);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		TextView view = new TextView(this);
		ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setGravity(Gravity.CENTER);
		view.setLayoutParams(params1);
		view.setText("No Network connection.");

		Button checkButton = new Button(this);
		ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		checkButton.setText("Refresh");
		checkButton.setLayoutParams(params2);
		checkButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(AppUtils.isNetworkOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
				{

					displayMainScreen();
				}
			}
		});
		layout.addView(view);
		layout.addView(checkButton);
		setContentView(layout);
	}

	private void displayMainScreen()
	{

		setContentView(R.layout.activity_main);
		journeyDateButton = (Button) findViewById(R.id.journeyDateButton);
		if(journeyDateStr!=null)
		{
			journeyDateButton.setText(journeyDateStr);
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			Date newDate = cal.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
			journeyDateButton.setText(formatter.format(newDate));	
		}

		requestQueue = Volley.newRequestQueue(getApplicationContext());

		progress = new ProgressDialog(this);
		progress.setIndeterminate(true);

		// Look up the AdView as a resource and load a request.
		adView = (AdView)this.findViewById(R.id.adMobView);
		AdRequest adRequest = new AdRequest.Builder()
		.build();
		adView.loadAd(adRequest);
	}

	/**
	 * This method will handle the From search button click events.
	 * It will search for bus stations starting with the string entered in
	 * the From text view and show a popup dialog to select a station. 
	 * @param view
	 */

	public void fromSearchButtonClickHandler(View view)
	{
		fromTextView = (EditText) findViewById(R.id.fromAuto);
		if(fromTextView.getText().toString().trim().length()<3)
		{
			Toast.makeText(this, R.string.station_field_validation, Toast.LENGTH_LONG).show();
			return;
		}

		progress.setMessage(getString(R.string.searching));
		progress.show();
		String FROM_URL = AppUtils.FROM_URL;
		FROM_URL += fromTextView.getText();
		StringRequest strRequest = new StringRequest(FROM_URL, fromSeachSuccessHandler(), requestErrorHandler());
		strRequest.setShouldCache(false);
		requestQueue.add(strRequest);
	}

	/**
	 * This method will handle the To search button click events.
	 * It will search for bus stations starting with the string entered in
	 * the To text view and show a popup dialog to select a station. 
	 * @param view
	 */

	public void toSearchButtonClickHandler(View view)
	{
		toTextView = (EditText) findViewById(R.id.toAuto);

		if(fromServiceInfo==null || fromServiceInfo.getServiceCode()==null || fromServiceInfo.getServiceCode().length()<=0)
		{
			Toast.makeText(this, "Please enter valid 'From' station.", Toast.LENGTH_LONG).show();
			return;
		}

		if(toTextView.getText().toString().trim().length()<3)
		{
			Toast.makeText(this, R.string.station_field_validation, Toast.LENGTH_LONG).show();
			return;
		}
		progress.setMessage(getString(R.string.searching));
		progress.show();
		String TO_URL = AppUtils.TO_URL+toTextView.getText()+"&startPlaceId="+fromServiceInfo.getServiceId();
		StringRequest strRequest = new StringRequest(TO_URL, toSeachSuccessHandler(), requestErrorHandler());
		strRequest.setShouldCache(false);
		requestQueue.add(strRequest);
	}

	/**
	 * Handle the journey date button click events.
	 * @param view
	 */
	public void dateButtonClickHandler(View view)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		Calendar cal = null;
		try {
			Date date = formatter.parse(journeyDateButton.getText().toString());
			cal = Calendar.getInstance();
			cal.setTime(date);
		} catch (ParseException e) {
			Log.e("Error", e.getMessage());
		}

		DatePickerFragment newFragment = new DatePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("Calendar", cal);
		newFragment.setArguments(bundle);
		newFragment.setOnDateSetListener(this);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	/**
	 * Increment date by one day and update the journey field
	 * @param view
	 */
	public void nextDateButtonClickHandler(View view)
	{
		journeyDateButton.setText(AppUtils.getNewDate(1,journeyDateButton.getText().toString()));
	}

	/**
	 * Decrement date by one day and update the journey field
	 * @param view
	 */
	public void previousDateButtonClickHandler(View view)
	{
		journeyDateButton.setText(AppUtils.getNewDate(-1,journeyDateButton.getText().toString()));
	}

	/**
	 * This method will handle the Main Search button click events.
	 * It will search for bus services running with the selected
	 * From and To stations and will show the result in a separate activity. 
	 * @param view
	 */

	public void searchButtonClickHandler(View v) {

		Button journeyDateButton = (Button) findViewById(R.id.journeyDateButton);
		Spinner spinner = (Spinner) findViewById(R.id.serviceTypeCombo);

		String dateStr = journeyDateButton.getText().toString();
		String serviceType = (String) spinner.getSelectedItem();

		if(fromServiceInfo==null || fromServiceInfo.getServiceCode()==null || fromServiceInfo.getServiceCode().length()<=0)
		{
			Toast.makeText(this, "Please enter valid 'From' station.", Toast.LENGTH_LONG).show();
			return;
		}
		if(toServiceInfo==null || toServiceInfo.getServiceCode()==null || toServiceInfo.getServiceCode().length()<=0)
		{
			Toast.makeText(this, "Please enter valid 'To' station.", Toast.LENGTH_LONG).show();
			return;
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		try {
			Date date = formatter.parse(dateStr);
			cal.setTime(date);
			if(cal.before(Calendar.getInstance().getTime()))
			{
				Toast.makeText(this, R.string.journey_date_validation, Toast.LENGTH_LONG).show();
				return;
			}
		} catch (ParseException e) {
			Log.e("Error", e.getLocalizedMessage());
		}

		short serviceClassId = 0;
		if(serviceType.equals("A/C Service"))
		{
			serviceClassId = 200;
		}
		else
		{
			serviceClassId = 201;
		}

		progress.setMessage(getString(R.string.searching));
		progress.show();

		String url = "serviceClassId="+serviceClassId+"&concessionId=1347688949874&" +
				"txtJourneyDate="+dateStr+"&txtReturnJourneyDate="+ dateStr +
				"&searchType=0&startPlaceId="+fromServiceInfo.getServiceId()+
				"&endPlaceId="+toServiceInfo.getServiceId();

		StringRequest strRequest = new StringRequest(AppUtils.SEARCH_URL+url, mainSeachSuccessHandler(), requestErrorHandler());
		strRequest.setShouldCache(false);
		requestQueue.add(strRequest);
	}

	private Response.Listener<String> fromSeachSuccessHandler() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				progress.cancel();
				showListView(response,"Select From Station","FROM");
			}
		};
	}

	private Response.Listener<String> toSeachSuccessHandler() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				progress.cancel();
				showListView(response,"Select To Station","TO");
			}
		};
	}

	protected void showListView(String response, final String title,final String stationType) {
		List<ServiceInfo> stationList = AppUtils.parseBusStations(response);
		final Dialog listDialog = new Dialog(MainActivity.this);
		listDialog.setTitle(title);
		LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.station_list, null,false);
		listDialog.setContentView(view);
		listDialog.setCancelable(true);

		final ListView listView = (ListView) listDialog.findViewById(R.id.stationListView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ServiceInfo info = (ServiceInfo) listView.getItemAtPosition(position);
				if(stationType.equals("FROM"))
				{
					EditText textView = (EditText) findViewById(R.id.fromAuto);
					textView.setText(info.getServiceName());
					fromServiceInfo = info;	
				}
				else
				{
					EditText textView = (EditText) findViewById(R.id.toAuto);
					textView.setText(info.getServiceName());
					toServiceInfo = info;
				}
				listDialog.cancel();
			}
		});
		listView.setAdapter(new ArrayAdapter<ServiceInfo>(MainActivity.this, android.R.layout.simple_list_item_1, stationList));
		listDialog.show();
	}

	private Response.Listener<String> mainSeachSuccessHandler() {

		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				progress.cancel();
				ArrayList<SearchResultVO> list = AppUtils.formatData(response);
				SearchResultVO resultVO = new SearchResultVO();
				resultVO.setServiceName("Service");
				resultVO.setDeparture("Departure");
				resultVO.setArrival("Arrival");
				resultVO.setAvailableSeats("Seats");
				resultVO.setAdultFare("Fare");
				list.add(0, resultVO);
				Intent intent = new Intent().setClass(MainActivity.this, SearchResultListActivity.class);
				intent.putParcelableArrayListExtra("SearchResults",list);
				intent.putExtra("FROM", fromServiceInfo.getServiceName());
				intent.putExtra("TO", toServiceInfo.getServiceName());
				intent.putExtra("DATE", journeyDateButton.getText().toString());
				MainActivity.this.startActivityForResult(intent, 0);
			}
		};
	}

	private Response.ErrorListener requestErrorHandler() {

		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				progress.cancel();
				Toast.makeText(MainActivity.this, R.string.communication_error, Toast.LENGTH_SHORT).show();
				Log.e("VOLLEY COMMUNICATION ERROR", error.toString());
			}
		};
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		if(cal.before(Calendar.getInstance().getTime()))
		{
			Toast.makeText(this, R.string.journey_date_validation, Toast.LENGTH_LONG).show();
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		journeyDateButton.setText(formatter.format(cal.getTime()));
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable("fromServiceInfo", fromServiceInfo);
		bundle.putSerializable("toServiceInfo",toServiceInfo);
		bundle.putString("journeyDate", journeyDateButton.getText().toString());
		super.onSaveInstanceState(bundle);
	}

	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		fromServiceInfo = (ServiceInfo) bundle.getSerializable("fromServiceInfo");
		toServiceInfo = (ServiceInfo) bundle.getSerializable("toServiceInfo");
		journeyDateStr = bundle.getString("journeyDate");
		super.onRestoreInstanceState(bundle);
	}
}
