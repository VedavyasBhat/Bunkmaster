package com.example.bunkmasterfull.subjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;

public class SubjectsArrayAdapter extends ArrayAdapter<String> 
{
	private final Context context;
	private final String[] values;
	 
	public SubjectsArrayAdapter(Context context, String[] values) 
	{
		super(context, R.layout.subject_list_view, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.subject_list_view, parent, false);
		PieChart pi = (PieChart) rowView.findViewById(R.id.pi);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		
		textView.setText(values[position]);

		String sub = values[position];
		DBHelper db = new DBHelper(context, null, null, 1);
		float held = db.getHeld(sub);
		float att = db.getAttended(sub);
		float per = (att/held) * 100;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		//ImageView img = (ImageView) ((ViewGroup) rowView).getChildAt(0);
		/*if(per <= Integer.parseInt(sp.getString("criteria", "75")))
			rowView.setBackgroundResource(R.drawable.list_item_danger);
		else
			rowView.setBackgroundResource(R.drawable.tile); */
		
		pi.setPercentage(per);
		return rowView;
	}
}