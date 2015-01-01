package com.smart.apsrtcbus.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smart.apsrtcbus.R;
import com.smart.apsrtcbus.vo.AttributeNameValue;

public class JourneyDetailsAdapter extends ArrayAdapter<AttributeNameValue>{

	private Context context = null;
	private List<AttributeNameValue> list = null;

	public JourneyDetailsAdapter(Context context, List<AttributeNameValue> list) {
		super(context, R.layout.listview_row,list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.journey_details_row, parent, false);
		}

		TextView nameView = (TextView) convertView.findViewById(R.id.nameView);
		TextView detailsView = (TextView) convertView.findViewById(R.id.detailsView);
		nameView.setText(list.get(position).getName());
		detailsView.setText(list.get(position).getValue());
		
		return convertView;
	}
}

