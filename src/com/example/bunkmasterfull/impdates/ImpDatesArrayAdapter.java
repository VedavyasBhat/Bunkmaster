package com.example.bunkmasterfull.impdates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;

public class ImpDatesArrayAdapter extends ArrayAdapter<String> 
{
	private final Context context;
	private final String[] values;
	 
	public ImpDatesArrayAdapter(Context context, String[] values) 
	{
		super(context, R.layout.impdates_list_view, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//init variables
		View rowView = inflater.inflate(R.layout.impdates_list_view, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView tick = (ImageView) rowView.findViewById(R.id.check);
		ImageView alarm = (ImageView) rowView.findViewById(R.id.alarm);
		
		//set label of the view
		textView.setText(values[position]);

		String impdate = values[position];
		DBHelper db = new DBHelper(context, null, null, 1);
		String[] dets = db.getImpDateDetails(impdate);
		
		if(dets[3].equals("0"))
			alarm.setVisibility(View.INVISIBLE);
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy");
		Calendar date = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		try
		{
			date.setTime(sdf.parse(dets[1]));
		} catch (ParseException e) {e.printStackTrace();}
		if(date.after(today))
			tick.setVisibility(View.INVISIBLE);
		
		rowView.setBackgroundResource(R.drawable.list_item);
		
		return rowView;
	}
}