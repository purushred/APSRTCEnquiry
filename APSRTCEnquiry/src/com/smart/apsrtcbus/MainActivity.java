package com.smart.apsrtcbus;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smart.apsrtcbus.utilities.AppUtils;
import com.smart.apsrtcbus.vo.ServiceInfo;

public class MainActivity extends Activity {

	private RequestQueue requestQueue = null;

	private AutoCompleteTextView fromTextView;
	private AutoCompleteTextView toTextView;

	private ServiceInfo fromServiceInfo = null;
	private ServiceInfo toServiceInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestQueue = Volley.newRequestQueue(getApplicationContext());
	}

	/**
	 * This method will handle the From search button click events.
	 * It will search for bus stations starting with the string entered in
	 * the From text view and show a popup dialog to select a station. 
	 * @param view
	 */

	public void fromSearchButtonClickHandler(View view)
	{
		fromTextView = (AutoCompleteTextView) findViewById(R.id.fromAuto);
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
		toTextView = (AutoCompleteTextView) findViewById(R.id.toAuto);
		String TO_URL = AppUtils.TO_URL+toTextView.getText()+"&startPlaceId=21101";
		StringRequest strRequest = new StringRequest(TO_URL, toSeachSuccessHandler(), requestErrorHandler());
		strRequest.setShouldCache(false);
		requestQueue.add(strRequest);
	}

	/**
	 * This method will handle the Main Search button click events.
	 * It will search for bus services running with the selected
	 * From and To stations and will show the result in a separate activity. 
	 * @param view
	 */

	public void searchButtonClickHandler(View v) {

		EditText textView = (EditText) findViewById(R.id.journeyDateTextField);
		String date = textView.getText().toString();
		// serviceClassId 0 - All, 200 - A/C, 201 - Non-AC
		// Date DD/MM/YYYY format
		String url = "startPlaceId="+fromServiceInfo.getServiceId()+
				"&fromPlaceCode="+fromServiceInfo.getServiceCode()+
				"&fromPlaceName="+fromServiceInfo.getServiceName()+
				"&endPlaceId="+toServiceInfo.getServiceId()+
				"&toPlaceCode="+toServiceInfo.getServiceCode()+
				"&toPlaceName="+toServiceInfo.getServiceName()+
				"&maxSize=5&txtIDSrc=endPlaceId&txtCodeSrc=" +
				"toPlaceCode&txtNameSrc=toPlaceName&adultMale=1&childMale=0" +
				"&txtJourneyDate="+date+"&txtReturnJourneyDate="+date+"&serviceClassId=0&concessionId=" +
				//"29%2F1%2F2014&txtReturnJourneyDate=30%2F1%2F2014&serviceClassId=0&concessionId=" +
				"1347688949874&maxPassengerAllowed=0#1382114485666";
		StringRequest strRequest = new StringRequest(AppUtils.SEARCH_URL, mainSeachSuccessHandler(), requestErrorHandler());
		strRequest.setShouldCache(false);
		requestQueue.add(strRequest);
	}

	private Response.Listener<String> fromSeachSuccessHandler() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				List<ServiceInfo> stationList = AppUtils.parseBusStations(response);
				final Dialog listDialog = new Dialog(MainActivity.this);
				listDialog.setTitle("Select From Station");
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
						AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.fromAuto);
						textView.setText(info.getServiceName());
						fromServiceInfo = info;
						listDialog.cancel();
					}
				});
				listView.setAdapter(new ArrayAdapter<ServiceInfo>(MainActivity.this, android.R.layout.simple_list_item_1, stationList));
				listDialog.show();
			}
		};
	}

	private Response.Listener<String> toSeachSuccessHandler() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				List<ServiceInfo> stationList = AppUtils.parseBusStations(response);
				final Dialog listDialog = new Dialog(MainActivity.this);
				listDialog.setTitle("Select To Station");
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
						AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.toAuto);
						textView.setText(info.getServiceName());
						toServiceInfo = info;
						listDialog.cancel();
					}
				});
				listView.setAdapter(new ArrayAdapter<ServiceInfo>(MainActivity.this, android.R.layout.simple_list_item_1, stationList));
				listDialog.show();
			}
		};
	}

	private Response.Listener<String> mainSeachSuccessHandler() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				//Log.e("APSRTC", response.length()+"");
				response=response.replaceAll("\n", " ");
				response=response.replaceAll("\t", " ");
				Log.e("APSRTC",response.substring(response.length()-100, response.length()-1));
				if(response.contains("listAvailabileServices.jsp"))
				{
					Log.e("APSRTC", "TRUE");
				}
				else
				{
					Log.e("APSRTC", "FALSE");
				}
				/*String strArr[] = response.split("BoxBorder");
				if(strArr.length>1)
					Log.e("APSRTC",strArr[1]);*/
			}
		};
	}

	private Response.ErrorListener requestErrorHandler() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("APSRTC", error.getMessage());
			}
		};
	}
}
