package com.example.bunkmasterfull.options;

import java.util.Calendar;
import java.util.Set;

import com.example.bunkmasterfull.Monitor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Toast.makeText(getApplicationContext(), "Heelllooooo!", Toast.LENGTH_LONG).show();
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new Settings()).commit();
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		setAlarms();
	}
	
	public void setAlarms()
	{
		//get the week days for which low attendance alarms have to be set, and then set alarms for those days
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		Set<String> set;
		set=sp.getStringSet("lowattnotifs", null);
		if(set!=null)
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 19);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			Intent attintent = new Intent(this, Monitor.class);
			attintent.putExtra("attordate", 0);
			PendingIntent attpintent = PendingIntent.getService(this, 0, attintent, 0);
			
			alarm.cancel(attpintent);		//cancel all alarms for attendance reminders
			
			//check if set contains these days
			for(int i=0; i<7; i++)
			{
				if(set.contains(String.valueOf(i)))
				{
					cal.set(Calendar.DAY_OF_WEEK, i);
					alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), attpintent);
					Toast.makeText(getApplicationContext(), "Set for day: "+i, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
