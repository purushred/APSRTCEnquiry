package com.smart.apsrtcbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.apsrtcbus.vo.StationVO;

public class StationListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

	String type = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_list);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		type = getIntent().getStringExtra("Type");
		if(type.equals("From"))
		{
			setTitle("Select From Station");
		}
		else
		{
			setTitle("Select To Station");
		}
		final ArrayAdapter<StationVO> adapter = new ArrayAdapter<StationVO>  
		(this,android.R.layout.simple_list_item_1,MainActivity.stationList);

		ListView stationListView = (ListView) findViewById(R.id.stationListView);
		stationListView.setAdapter(adapter);
		stationListView.setOnItemClickListener(this);

		EditText searchView = (EditText) findViewById(R.id.historySearchView);
		searchView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
				if (adapter != null && charSequence != null)
					adapter.getFilter().filter(charSequence.toString());
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

		StationVO item = (StationVO) adapterView.getItemAtPosition(position);
		int serviceInfoLocation = MainActivity.stationList.indexOf(item);
		StationVO serviceInfo = MainActivity.stationList.get(serviceInfoLocation);

		Intent intent = new Intent();
		intent.putExtra("ServiceInfo", serviceInfo);
		intent.putExtra("Type", type);
		setResult(RESULT_OK, intent);
		finish();
	}
}
