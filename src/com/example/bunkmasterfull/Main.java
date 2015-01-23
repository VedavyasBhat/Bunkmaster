package com.example.bunkmasterfull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.bunkmasterfull.impdates.ShowImpDates;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;
import com.example.bunkmasterfull.semdates.SemDates;
import com.example.bunkmasterfull.subjects.ShowSubjects;
import com.example.bunkmasterfull.timetable.AcceptTimetable;
import com.example.bunkmasterfull.weekview.WeekView;

public class Main extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setAlarms();
		testMonitor();
		
		final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		Calendar today = Calendar.getInstance();
		
		if(sp.getString("semstart", null) == null)
			sp.edit().putString("semstart", sdf.format(today.getTime())).apply();
		
		if(sp.getString("semend", null) == null)
			sp.edit().putString("semend", sdf.format(today.getTime())).apply();
		
		if(sp.getString("lastcalculated", null) == null)
			sp.edit().putString("lastcalculated", sp.getString("semstart", null)).apply();
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
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		new Thread()
		{
			public void run()
			{
				DBHelper db=new DBHelper(Main.this, null, null, 1);
		    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	
		    	Update update = new Update(db, sp);
		    	update.update();
		    	Calendar cal = Calendar.getInstance();
		    	cal.set(2014, Calendar.APRIL, 4);
		    	//update.dailyUpdate(cal);
				db.close();
			}
		}.start();
		testMonitor();
	}
	
	public void testMonitor()
	{
		Intent intent = new Intent(this, Monitor.class);
		intent.putExtra("service", 0);   //0 is to update attendance
		startService(intent);
	}
	
	public void setAlarms()
	{
		//make an alarm to run the Monitor service at 7:00 pm everyday, to:
		//	update attendance
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		Intent dateintent = new Intent(this, Monitor.class);
		dateintent.putExtra("service", 2);		//2 is for updating attendance
		PendingIntent datepintent = PendingIntent.getService(this, 0, dateintent, 0);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		//set alarms according to days
		Calendar al = Calendar.getInstance();
		al.set(Calendar.HOUR_OF_DAY, 19);
		al.set(Calendar.MINUTE, 0);
		al.set(Calendar.SECOND, 0);		//alarm at 7:00 pm
		
		//important date check to run everyday at 7:00pm
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, al.getTimeInMillis(), AlarmManager.INTERVAL_DAY, datepintent);
	}
	
	public void startShowSubjects(View view)
	{
		startActivity(new Intent(this, ShowSubjects.class));
	}
	
	public void startAcceptTimetable(View view)
	{
		startActivity(new Intent(this, AcceptTimetable.class));
	}
	
	public void startWeekView(View view)
	{
		startActivity(new Intent(this, WeekView.class));
	}
	
	public void startImportantDates(View view)
	{
		startActivity(new Intent(this, ShowImpDates.class));
	}
	
	public void startSemDates(View view)
	{
		startActivity(new Intent(this, SemDates.class));
	}
}