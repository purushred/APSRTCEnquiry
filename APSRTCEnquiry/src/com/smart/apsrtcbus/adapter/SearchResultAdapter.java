package com.smart.apsrtcbus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
		View rowView = null;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.listview_row, parent, false);
		}
		else
		{
			rowView = convertView;
		}

		TextView serviceNameView = (TextView) rowView.findViewById(R.id.serviceNameView);
		TextView departureView = (TextView) rowView.findViewById(R.id.departureView);
		TextView arrivalView = (TextView) rowView.findViewById(R.id.arrivalView);
		TextView availableSeatsView = (TextView) rowView.findViewById(R.id.availableSeatsView);
		TextView fareView = (TextView) rowView.findViewById(R.id.fareView);
		serviceNameView.setText(list.get(position).getServiceName());
		departureView.setText(list.get(position).getDeparture());
		arrivalView.setText(list.get(position).getArrival());
		String seats = list.get(position).getAvailableSeats();
		availableSeatsView.setText(seats);
		fareView.setText(list.get(position).getAdultFare());

		GradientDrawable bgShape = (GradientDrawable) availableSeatsView.getBackground();

		if(seats.contains("Seats"))
		{
			bgShape.setColor(context.getResources().getColor(R.color.white_color));
			availableSeatsView.setTextColor(context.getResources().getColor(R.color.black_color));
		}
		else if(seats.contains("WL"))
		{
			bgShape.setColor(context.getResources().getColor(R.color.red_color));
		}
		else {
			try {
				availableSeatsView.setTextColor(context.getResources().getColor(R.color.white_color));
				int seatNo = Integer.parseInt(seats);
				if (seatNo < 10) {
					bgShape.setColor(context.getResources().getColor(R.color.red_color));
				} else if (seatNo < 20) {
					bgShape.setColor(context.getResources().getColor(R.color.blue_color));
				} else {
					bgShape.setColor(context.getResources().getColor(R.color.green_color));
				}
			}
			catch(Exception ex){
			}
			
		}
		return rowView;
	}
}
