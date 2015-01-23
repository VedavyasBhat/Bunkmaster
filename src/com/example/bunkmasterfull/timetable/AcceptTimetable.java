package com.example.bunkmasterfull.timetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;


public class AcceptTimetable extends Activity
{	
	Spinner hours[][]=new Spinner[6][6];
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accept_timetable);
		
		hours[0][0]=(Spinner)findViewById(R.id.ttmon1);  hours[3][0]=(Spinner)findViewById(R.id.ttthu1);
		hours[0][1]=(Spinner)findViewById(R.id.ttmon2);  hours[3][1]=(Spinner)findViewById(R.id.ttthu2);
		hours[0][2]=(Spinner)findViewById(R.id.ttmon3);  hours[3][2]=(Spinner)findViewById(R.id.ttthu3);
		hours[0][3]=(Spinner)findViewById(R.id.ttmon4);  hours[3][3]=(Spinner)findViewById(R.id.ttthu4);
		hours[0][4]=(Spinner)findViewById(R.id.ttmon5);  hours[3][4]=(Spinner)findViewById(R.id.ttthu5);
		hours[0][5]=(Spinner)findViewById(R.id.ttmon6);  hours[3][5]=(Spinner)findViewById(R.id.ttthu6);
		hours[1][0]=(Spinner)findViewById(R.id.tttue1);  hours[4][0]=(Spinner)findViewById(R.id.ttfri1);
		hours[1][1]=(Spinner)findViewById(R.id.tttue2);  hours[4][1]=(Spinner)findViewById(R.id.ttfri2);
		hours[1][2]=(Spinner)findViewById(R.id.tttue3);  hours[4][2]=(Spinner)findViewById(R.id.ttfri3);
		hours[1][3]=(Spinner)findViewById(R.id.tttue4);  hours[4][3]=(Spinner)findViewById(R.id.ttfri4);
		hours[1][4]=(Spinner)findViewById(R.id.tttue5);  hours[4][4]=(Spinner)findViewById(R.id.ttfri5);
		hours[1][5]=(Spinner)findViewById(R.id.tttue6);  hours[4][5]=(Spinner)findViewById(R.id.ttfri6);
		hours[2][0]=(Spinner)findViewById(R.id.ttwed1);  hours[5][0]=(Spinner)findViewById(R.id.ttsat1);
		hours[2][1]=(Spinner)findViewById(R.id.ttwed2);  hours[5][1]=(Spinner)findViewById(R.id.ttsat2);
		hours[2][2]=(Spinner)findViewById(R.id.ttwed3);  hours[5][2]=(Spinner)findViewById(R.id.ttsat3);
		hours[2][3]=(Spinner)findViewById(R.id.ttwed4);  hours[5][3]=(Spinner)findViewById(R.id.ttsat4);
		hours[2][4]=(Spinner)findViewById(R.id.ttwed5);  hours[5][4]=(Spinner)findViewById(R.id.ttsat5);
		hours[2][5]=(Spinner)findViewById(R.id.ttwed6);  hours[5][5]=(Spinner)findViewById(R.id.ttsat6);
		
		DBHelper db=new DBHelper(this, null, null, 1);
		String n[]=db.getAllNames();
		
		String names[]=new String[n.length+1];
		names[0]="free";
		for(int i=0; i<n.length; i++)
			names[i+1]=n[i];
		
		//names is the array with "free" as the first hour
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				hours[i][j].setAdapter(adapter);
		
		db.close();
		
		setTimetableView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.action_settings_all, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else if (itemId == R.id.action_help) {
			startActivity(new Intent(this, Help.class));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void setTimetableView()
	{
		DBHelper db=new DBHelper(this, null, null, 1);
		Timetable tt =  db.getTimetable();
		int pos = 0;
		
		for(int i=0; i<6; i++)
		{
			for(int j=0; j<6; j++)
			{
				pos = adapter.getPosition(tt.getHour(i, j));
				hours[i][j].setSelection(pos);
			}
		}
		db.close();
	}
	
	public void done(View view)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				DBHelper db=new DBHelper(AcceptTimetable.this, null, null, 1);
				String []subs = db.getAllNames();
				int count[][] = new int[6][subs.length];
				
				for(int i=0; i<6; i++)
					for(int j=0; j<subs.length; j++)
						count[i][j]=0;
				
				String temp;
				for(int i=0; i<6; i++)
				{
					for(int j=0; j<6; j++)				//get selected items from the Spinners and change the database
					{
						temp = (String)hours[i][j].getSelectedItem();
						db.changeHour(i, j, temp);
					}
				}
				db.close();
			}
		}.start();
		finish();
	}
}