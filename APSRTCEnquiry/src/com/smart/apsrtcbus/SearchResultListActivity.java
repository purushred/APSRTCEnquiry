package com.smart.apsrtcbus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.apsrtcbus.adapter.SearchResultAdapter;
import com.smart.apsrtcbus.vo.SearchResultVO;

public class SearchResultListActivity extends ActionBarActivity{

	private AdView adView;

	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);

		setContentView(R.layout.search_results_list);

		ArrayList<SearchResultVO> list = this.getIntent().getParcelableArrayListExtra("SearchResults");
		final ListView listView = (ListView) findViewById(R.id.list);
		SearchResultAdapter adapter = new SearchResultAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	SearchResultVO resultVO = (SearchResultVO) listView.getAdapter().getItem(position);
        		Toast.makeText(SearchResultListActivity.this, "Departure @ "+ resultVO.getDeparture(), Toast.LENGTH_LONG).show();
            }
        });
		TextView fromTextView = (TextView) findViewById(R.id.fromTextView);
		TextView toTextView = (TextView) findViewById(R.id.toTextView);
		TextView dateTextView = (TextView) findViewById(R.id.dateTextView);

		fromTextView.setText(getIntent().getStringExtra("FROM"));
		toTextView.setText(getIntent().getStringExtra("TO"));

		String dateStr = getIntent().getStringExtra("DATE");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			Log.e("Error", e.getLocalizedMessage());
		}
		format.applyPattern("EEE dd MMM, yyyy");
		dateTextView.setText("JOURNEY DATE : "+format.format(date));

		// Look up the AdView as a resource and load a request.
		adView = (AdView)this.findViewById(R.id.adMobView1);
		AdRequest adRequest = new AdRequest.Builder()
		.build();
		adView.loadAd(adRequest);
	}


	public void backButtonClickHandler(View view)
	{
		finish();
	}

	public void refreshButtonClickHandler(View view)
	{
		//Toast.makeText(this, "Refresh feature coming soon..!", Toast.LENGTH_LONG).show();
	}

}
