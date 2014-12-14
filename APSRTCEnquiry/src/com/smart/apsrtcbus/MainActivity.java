package com.smart.apsrtcbus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.apsrtcbus.task.StationInfoAsyncTask;
import com.smart.apsrtcbus.utilities.AppUtils;
import com.smart.apsrtcbus.vo.SearchResultVO;
import com.smart.apsrtcbus.vo.StationVO;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MainActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, Callback{

	private TextView fromTextView;
	private TextView toTextView;
	private Button journeyDateButton;

	private StationVO fromServiceInfo = null;
	private StationVO toServiceInfo = null;

	private ProgressDialog progress = null;
	private short serviceClassId = 0;

	public static List<StationVO> stationList;
	private OkHttpClient httpClient = null;
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!AppUtils.isNetworkOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
		{
			buildNetworkErrorUI();
		}
		else
		{
			displayMainScreen();
		}
	}

	/**
	 * To display error message in case of any network errors.
	 */
	private void buildNetworkErrorUI() {

		LinearLayout layout = new LinearLayout(this);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.VERTICAL);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		TextView view = new TextView(this);
		ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setGravity(Gravity.CENTER);
		view.setLayoutParams(params1);
		view.setText("No Network connection.");

		Button checkButton = new Button(this);
		ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
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

	/**
	 * To display the main screen
	 */
	private void displayMainScreen()
	{
		setContentView(R.layout.activity_main);
		journeyDateButton = (Button) findViewById(R.id.journeyDateButton);
		httpClient = new OkHttpClient();
		handler = new Handler();
		try {
			getStationInfo();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getFromDB();

		progress = new ProgressDialog(this);
		progress.setIndeterminate(true);


		AdView adView = (AdView)this.findViewById(R.id.adMobView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	private void getStationInfo() throws IOException
	{
		SharedPreferences pref =  getPreferences(MODE_PRIVATE);
		String jsonData = pref.getString("STATION_LIST", null);

		if(jsonData==null)
		{
			new StationInfoAsyncTask(this).execute();
		}
		else
		{
			stationList = AppUtils.getBusStationList(jsonData);
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

			progressBar.setVisibility(View.GONE);
			relativeLayout.setVisibility(View.VISIBLE);
		}
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
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

		String dateStr = journeyDateButton.getText().toString();

		if(fromServiceInfo==null || fromServiceInfo.getId()==null || fromServiceInfo.getId().length()<=0)
		{
			Toast.makeText(this, "Please enter valid 'From' station.", Toast.LENGTH_LONG).show();
			return;
		}
		if(toServiceInfo==null || toServiceInfo.getId()==null || toServiceInfo.getId().length()<=0)
		{
			Toast.makeText(this, "Please enter valid 'To' station.", Toast.LENGTH_LONG).show();
			return;
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		try {
			Date date = formatter.parse(dateStr);
			cal.setTime(date);
			Calendar today = Calendar.getInstance();
			String str = formatter.format(new Date());
			today.setTime(formatter.parse(str));
			if(cal.compareTo(today)<0)
			{
				Toast.makeText(this, R.string.journey_date_validation, Toast.LENGTH_LONG).show();
				return;
			}
		} catch (ParseException e) {
			Log.e("Error", e.getLocalizedMessage());
		}

		if(radioGroup.getCheckedRadioButtonId()==R.id.allService)
		{
			serviceClassId = 0;
		}
		else if(radioGroup.getCheckedRadioButtonId()==R.id.acService)
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
				"&searchType=0&startPlaceId="+fromServiceInfo.getId()+
				"&endPlaceId="+toServiceInfo.getId();

		storeInDB(serviceClassId,dateStr,fromServiceInfo,toServiceInfo);
		try {
			getServiceData(AppUtils.SEARCH_URL+url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Test", e.getMessage());
		}
	}

	private void getServiceData(String url) throws IOException{

		Request request = new Request.Builder().url(url).build();
		httpClient.newCall(request).enqueue(this);
	}

	/**
	 * To get the data from cached local database.
	 */
	private void getFromDB(){
		SharedPreferences pref =  getPreferences(MODE_PRIVATE);
		String searchData = pref.getString("LAST_SEARCH_DATA", null);
		fromTextView = (TextView) findViewById(R.id.fromAuto);
		toTextView = (TextView) findViewById(R.id.toAuto);

		fromTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), StationListActivity.class);
				intent.putExtra("Type", "From");
				startActivityForResult(intent,2);
			}
		});

		toTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), StationListActivity.class);
				intent.putExtra("Type", "To");
				startActivityForResult(intent,2);
			}
		});

		if(searchData!=null)
		{
			String[] dataArr = searchData.split("#");
			String serviceClassIdStr = dataArr[0];

			if(serviceClassIdStr.equals("0"))
			{
				RadioButton radioButton = (RadioButton) findViewById(R.id.allService);
				radioButton.setSelected(true);
				serviceClassId = 0;
			}
			else if(serviceClassIdStr.equals("200"))
			{
				RadioButton radioButton = (RadioButton) findViewById(R.id.acService);
				radioButton.setSelected(true);
				serviceClassId = 200;
			}
			else
			{
				RadioButton radioButton = (RadioButton) findViewById(R.id.nonAcService);
				radioButton.setSelected(true);
				serviceClassId = 201;
			}

			Button journeyDateButton = (Button) findViewById(R.id.journeyDateButton);
			journeyDateButton.setText(dataArr[1]);

			fromServiceInfo = new StationVO(dataArr[2], dataArr[3]);
			toServiceInfo = new StationVO(dataArr[4], dataArr[5]);

			fromTextView.setText(fromServiceInfo.getValue());
			toTextView.setText(toServiceInfo.getValue());
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			Date newDate = cal.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
			journeyDateButton.setText(formatter.format(newDate));	
		}
	}

	/**
	 * To store the last search information in local cache to 
	 * get display when launched next time.
	 * @param serviceClassId
	 * @param dateStr
	 * @param fromServiceInfo
	 * @param toServiceInfo
	 */
	private void storeInDB(short serviceClassId, String dateStr, 
			StationVO fromServiceInfo, StationVO toServiceInfo) {
		SharedPreferences pref =  getPreferences(MODE_PRIVATE);
		Editor editor = pref.edit();
		String dataStr = serviceClassId + "#" + dateStr +"#" + 
				fromServiceInfo.getId()
				+ "#"+fromServiceInfo.getValue() + "#"
				+ toServiceInfo.getId()
				+ "#" +toServiceInfo.getValue();
		editor.putString("LAST_SEARCH_DATA", dataStr);
		editor.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode==RESULT_OK)
		{
			StationVO serviceInfo = (StationVO) intent.getSerializableExtra("ServiceInfo");
			String type = intent.getStringExtra("Type");
			if(type.equals("From"))
			{
				fromServiceInfo = serviceInfo;
				fromTextView.setText(fromServiceInfo.toString());
			}
			else if(type.equals("To")) {
				toServiceInfo = serviceInfo;
				toTextView.setText(toServiceInfo.toString());
			}
		}
	};


	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		if(cal.compareTo(Calendar.getInstance())<0)
		{
			Toast.makeText(this, R.string.journey_date_validation, Toast.LENGTH_LONG).show();
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		journeyDateButton.setText(formatter.format(cal.getTime()));
	}


	@Override
	public void onFailure(Request request, IOException exception) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), "Communication error occured. Try again..", Toast.LENGTH_SHORT).show();
			} 
		});
	}

	@Override
	public void onResponse(Response response) throws IOException {

		progress.cancel();
		ArrayList<SearchResultVO> list = AppUtils.formatData(response.body().string());
		SearchResultVO resultVO = new SearchResultVO();
		resultVO.setServiceName("Service");
		resultVO.setDeparture("Departure");
		resultVO.setArrival("Arrival");
		resultVO.setAvailableSeats("Seats");
		resultVO.setAdultFare("Fare");
		list.add(0, resultVO);
		Intent intent = new Intent().setClass(MainActivity.this, SearchResultListActivity.class);
		intent.putParcelableArrayListExtra("SearchResults",list);
		intent.putExtra("FROM", fromServiceInfo.getValue());
		intent.putExtra("TO", toServiceInfo.getValue());
		intent.putExtra("DATE", journeyDateButton.getText().toString());
		startActivity(intent);
	}
}
