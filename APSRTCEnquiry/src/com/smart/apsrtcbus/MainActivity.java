package com.smart.apsrtcbus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
	private String searchURL;
	private final long DAY_IN_MILLI_SEC = 3*86400000; //24 * 3600 * 1000;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!AppUtils.isNetworkOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
			buildNetworkErrorUI();
		else
			displayMainScreen();
	}

	/**
	 * To display error message in case of any network errors.
	 */
	private void buildNetworkErrorUI() {

		setContentView(R.layout.activity_network_error);
		Button retryButton = (Button) findViewById(R.id.retryButton);
		retryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(AppUtils.isNetworkOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
				{
					displayMainScreen();
				}
			}
		});
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
			getStationList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getLastSearchParams();

		progress = new ProgressDialog(this);
		progress.setIndeterminate(true);

		AdView adView = (AdView)this.findViewById(R.id.adMobView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	private void getStationList() throws IOException
	{
		SharedPreferences pref =  getPreferences(MODE_PRIVATE);
		String jsonData = pref.getString("STATION_LIST", null);
		long lastSyncTimeStamp = pref.getLong("LAST_SYNC_TIMESTAMP", 0);
		long currentTimeStamp = new Date().getTime();
		
		if(jsonData==null || (currentTimeStamp-lastSyncTimeStamp>=DAY_IN_MILLI_SEC))
		{
			new StationInfoAsyncTask(this).execute();
		}
		else
		{
			stationList = AppUtils.getBusStationList(jsonData);
			if(stationList == null || stationList.size()<=0){
				new StationInfoAsyncTask(this).execute();
			}else {
				ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
				RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

				progressBar.setVisibility(View.GONE);
				relativeLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	public void buttonClickHandler(View view){

		View nextDateButton = findViewById(R.id.nextDateButton);
		View prevDateButton = findViewById(R.id.previousDateButton);
		View searchButton = findViewById(R.id.searchButton);

		if(searchButton == view){
			searchHandler();
		} else if(journeyDateButton == view){

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
		else if(nextDateButton == view){
			journeyDateButton.setText(AppUtils.getNewDate(1,journeyDateButton.getText().toString()));
		}
		else if(prevDateButton == view){
			journeyDateButton.setText(AppUtils.getNewDate(-1,journeyDateButton.getText().toString()));
		}
	}

	/**
	 * This method will handle the Main Search button click events.
	 * It will search for bus services running with the selected
	 * From and To stations and will show the result in a separate activity. 
	 * @param view
	 */
	private void searchHandler() {

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
			serviceClassId = 0;
		else if(radioGroup.getCheckedRadioButtonId()==R.id.acService)
			serviceClassId = 200;
		else
			serviceClassId = 201;

		progress.setMessage(getString(R.string.searching));
		progress.show();

		String url = "serviceClassId="+serviceClassId+"&concessionId=1347688949874&" +
				"txtJourneyDate="+dateStr+"&txtReturnJourneyDate="+ dateStr +
				"&searchType=0&startPlaceId="+fromServiceInfo.getId()+
				"&endPlaceId="+toServiceInfo.getId();

		storeInLocalStorage(serviceClassId,dateStr,fromServiceInfo,toServiceInfo);

		searchURL = AppUtils.SEARCH_URL+url;
		Request request = new Request.Builder().url(searchURL).build();
		httpClient.newCall(request).enqueue(this);
	}

	/**
	 * To get the data from cached local database.
	 */
	private void getLastSearchParams(){
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

			Calendar cal = Calendar.getInstance();
			try {
				Date lastDate = formatter.parse(dataArr[1]);
				cal.setTime(lastDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(cal.compareTo(Calendar.getInstance())<0)	{
				journeyDateButton.setText(formatter.format(new Date()));
			}
			else {
				journeyDateButton.setText(dataArr[1]);
			}
			fromServiceInfo = new StationVO(dataArr[2], dataArr[3]);
			toServiceInfo = new StationVO(dataArr[4], dataArr[5]);

			fromTextView.setText(fromServiceInfo.getValue());
			toTextView.setText(toServiceInfo.getValue());
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			Date newDate = cal.getTime();
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
	private void storeInLocalStorage(short serviceClassId, String dateStr, 
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

		ArrayList<SearchResultVO> list = AppUtils.formatData(response.body().string());
		SearchResultVO resultVO = new SearchResultVO();
		resultVO.setServiceName("Service");
		resultVO.setDeparture("Departure");
		resultVO.setArrival("Arrival");
		resultVO.setAvailableSeats("Seats");
		resultVO.setAdultFare("Fare");
		list.add(0, resultVO);
		progress.cancel();
		Intent intent = new Intent().setClass(MainActivity.this, SearchResultListActivity.class);
		intent.putParcelableArrayListExtra("SearchResults",list);
		intent.putExtra("FROM", fromServiceInfo.getValue());
		intent.putExtra("TO", toServiceInfo.getValue());
		intent.putExtra("DATE", journeyDateButton.getText().toString());
		intent.putExtra("SEARCH_URL", searchURL);
		startActivity(intent);
	}
}
