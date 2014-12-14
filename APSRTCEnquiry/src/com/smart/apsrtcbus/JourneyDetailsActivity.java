package com.smart.apsrtcbus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.apsrtcbus.vo.SearchResultVO;

public class JourneyDetailsActivity extends ActionBarActivity{

	private AdView adView;

	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);

		setContentView(R.layout.activity_journey_details);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView fromTextView = (TextView) findViewById(R.id.textView001);
		TextView toTextView = (TextView) findViewById(R.id.textView002);
		TextView serviceNameTextView = (TextView) findViewById(R.id.textView12);
		TextView dateTextView = (TextView) findViewById(R.id.textView003);
		TextView depotTextView = (TextView) findViewById(R.id.textView22);
		TextView viaTextView = (TextView) findViewById(R.id.textView32);
		TextView distanceTextView = (TextView) findViewById(R.id.textView42);
		TextView departureTextView = (TextView) findViewById(R.id.textView52);
		TextView arrivalTextView = (TextView) findViewById(R.id.textView62);
		TextView adultTextView = (TextView) findViewById(R.id.textView72);
		TextView childTextView = (TextView) findViewById(R.id.textView82);
		TextView serviceTextView = (TextView) findViewById(R.id.textView92);
		TextView seatsTextView = (TextView) findViewById(R.id.textView102);
		
		SearchResultVO resultVO = (SearchResultVO) getIntent().getParcelableExtra("SearchResultVO");
		
		String dateStr = getIntent().getStringExtra("DATE");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			Log.e("Error", e.getLocalizedMessage());
		}
		format.applyPattern("EEE dd MMM, yyyy");
		fromTextView.setText(getIntent().getStringExtra("FROM"));
		toTextView.setText(getIntent().getStringExtra("TO"));
		dateTextView.setText(format.format(date));
		serviceNameTextView.setText(resultVO.getServiceName());
		depotTextView.setText(resultVO.getDepotName());
		viaTextView.setText(resultVO.getViaPlace());
		distanceTextView.setText(resultVO.getDistance()+"KM");
		departureTextView.setText(resultVO.getDeparture());
		arrivalTextView.setText(resultVO.getArrival());
		adultTextView.setText(resultVO.getAdultFare());
		childTextView.setText(resultVO.getChildFare());
		serviceTextView.setText(resultVO.getType());
		seatsTextView.setText(resultVO.getAvailableSeats());
		
		// Look up the AdView as a resource and load a request.
		adView = (AdView)this.findViewById(R.id.adMobView2);
		AdRequest adRequest = new AdRequest.Builder()
		.build();
		adView.loadAd(adRequest);
	}
}
