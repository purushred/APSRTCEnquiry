package com.smart.apsrtcbus;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.apsrtcbus.adapter.JourneyDetailsAdapter;
import com.smart.apsrtcbus.utilities.AppUtils;
import com.smart.apsrtcbus.vo.AttributeNameValue;
import com.smart.apsrtcbus.vo.SearchResultVO;

public class JourneyDetailsListActivity extends ActionBarActivity{

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.journey_details_list);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		List<AttributeNameValue> list = getAttributeList();
		
		final ListView listView = (ListView) findViewById(R.id.list);
		JourneyDetailsAdapter adapter = new JourneyDetailsAdapter(getApplicationContext(), list);
		listView.setAdapter(adapter);

		AdView adView = (AdView)this.findViewById(R.id.adMobView2);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	public void bookButtonClickHandler(View view){
		String url = getIntent().getStringExtra("SEARCH_URL");
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppUtils.MAIN_SEARCH_URL+url.split("\\?")[1]));
			startActivity(browserIntent);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Could not launch browser, Please try again.", Toast.LENGTH_LONG).show();
		}
	}
	
	private List<AttributeNameValue> getAttributeList() {
		
		SearchResultVO resultVO = (SearchResultVO) getIntent().getParcelableExtra("SearchResultVO");
		
		List<AttributeNameValue> list = new ArrayList<AttributeNameValue>(10);
		list.add(new AttributeNameValue("From",getIntent().getStringExtra("FROM")));
		list.add(new AttributeNameValue("To",getIntent().getStringExtra("TO")));
		list.add(new AttributeNameValue("Journey Date",AppUtils.getFormattedDate(getIntent().getStringExtra("DATE"))));
		list.add(new AttributeNameValue("Departure Time",resultVO.getDeparture()));
		list.add(new AttributeNameValue("Arrival Time",resultVO.getArrival()));
		list.add(new AttributeNameValue("Seats Available",resultVO.getAvailableSeats()));
		list.add(new AttributeNameValue("Service Name",resultVO.getServiceName()));
		list.add(new AttributeNameValue("Depot Name",resultVO.getDepotName()));
		list.add(new AttributeNameValue("Distance",resultVO.getDistance()+" KM"));
		list.add(new AttributeNameValue("Adult Fare",resultVO.getAdultFare()));
		list.add(new AttributeNameValue("Child Fare",resultVO.getChildFare()));
		
		return list;
	}

	
}
