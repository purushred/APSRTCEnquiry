package com.smart.apsrtcbus;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.smart.apsrtcbus.adapter.SearchResultAdapter;
import com.smart.apsrtcbus.vo.SearchResultVO;

public class SearchResultListActivity extends ListActivity{

	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	   ArrayList<SearchResultVO> list = this.getIntent().getParcelableArrayListExtra("SearchResults");
	    SearchResultAdapter adapter = new SearchResultAdapter(this, list);
	    setListAdapter(adapter);
	  }

	  @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    SearchResultVO item = (SearchResultVO) getListAdapter().getItem(position);
	    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	  }
}
