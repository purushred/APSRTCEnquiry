package com.smart.apsrtcbus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smart.apsrtcbus.R;
import com.smart.apsrtcbus.vo.SearchResultVO;

public class SearchResultAdapter extends ArrayAdapter<SearchResultVO>{

	private Context context = null;
	private ArrayList<SearchResultVO> list = null;
	
	public SearchResultAdapter(Context context, ArrayList<SearchResultVO> list) {
		super(context, R.layout.listview_row,list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.listview_row, parent, false);
		    TextView serviceNameView = (TextView) rowView.findViewById(R.id.serviceNameView);
		    TextView departureView = (TextView) rowView.findViewById(R.id.departureView);
		    TextView arrivalView = (TextView) rowView.findViewById(R.id.arrivalView);
		    TextView availableSeatsView = (TextView) rowView.findViewById(R.id.availableSeatsView);
		    TextView fareView = (TextView) rowView.findViewById(R.id.fareView);
		    serviceNameView.setText(list.get(position).getServiceName());
		    departureView.setText(list.get(position).getDeparture());
		    arrivalView.setText(list.get(position).getArrival());
		    availableSeatsView.setText(list.get(position).getAvailableSeats());
		    fareView.setText(list.get(position).getAdultFare());
		    return rowView;
	}
}
