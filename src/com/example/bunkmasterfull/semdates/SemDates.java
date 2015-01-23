package com.example.bunkmasterfull.semdates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

@SuppressLint("ValidFragment")
public class SemDates extends Activity 
{
	TextView starttv, endtv;
	Button changestart, changeend;
	Calendar st, en;
	DatePicker startdp, enddp;
	SharedPreferences semdates;
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("dd-M-yyyy");
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sem_dates);
		// Show the Up button in the action bar.
		setupActionBar();
		
		initVariables();
		setCurrentSemDates();
		setDatePicker();
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() 
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
	
	public void initVariables()
	{
		starttv = (TextView)findViewById(R.id.startdate);
		endtv = (TextView)findViewById(R.id.enddate);
		changestart = (Button)findViewById(R.id.changestart);
		changeend = (Button)findViewById(R.id.changeend);
		
		semdates = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());  //obtain shared preference
	}
	
	public void setCurrentSemDates()
	{
		Calendar calst = Calendar.getInstance();
		Calendar calen = Calendar.getInstance();
		
		String start = semdates.getString("semstart", null);
		String end = semdates.getString("semend", null);
		
		try 
		{
			calst.setTime(sdf1.parse(start));
			calen.setTime(sdf1.parse(end));
		} catch (ParseException e1) {e1.printStackTrace();}
		
		starttv.setText(sdf.format(calst.getTime())); //setting current start and end dates to the textviews
		endtv.setText(sdf.format(calen.getTime()));
		
	}
	
	public void setDatePicker()
	{
		View.OnClickListener showDatePicker = new View.OnClickListener() 
		{
		    @Override
		    public void onClick(View v) 
		    {
		        final View vv = v;
		        String date;
		        if(v.getId() == R.id.changestart) date = (String) starttv.getText();
		        else date = (String) endtv.getText();
		        
		        Calendar cal = Calendar.getInstance();
		        try {	cal.setTime(sdf1.parse(date));	}
		        catch (ParseException e) {e.printStackTrace();	}
		        
		        DatePickerDialog datePickerDialog = new DatePickerDialog(SemDates.this, new DatePickerDialog.OnDateSetListener() 
		        {
		            @Override
		            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		            {
	                	String key;
	                	TextView t;
		                if (vv.getId() == R.id.changestart)
		                {
		                	key = "semstart";
		                	t = starttv;
		                }
		                else
		                {
		                	key = "semend";
		                	t = endtv;
		                }
		                
		                Calendar c = Calendar.getInstance();
	                	c.set(year, monthOfYear, dayOfMonth);
	                	Calendar today = Calendar.getInstance();
	                	boolean flag = true;
	                	if(t == starttv)	//if date entered is for start of sem
	                	{
	                		if(c.after(today))	//if date is after today
	                		{
	                			CharSequence text = "Start of the semester cannot be after the current date. " +
	                					"Please choose an appropriate date.";
	                			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	                			flag = false;
	                		}
	                	}
	                	else			//if date entered is for end of sem
	                	{
	                		if(c.before(today))	//if date is before today
	                		{
	                			CharSequence text = "End of the semester cannot be before the current date. " +
	                					"Please choose an appropriate date.";
	                			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	                			flag = false;
	                		}
	                	}
	                	
	                	if(flag)
	                	{
	                		SimpleDateFormat ssdf = new SimpleDateFormat("dd MMM yyyy");
	                		semdates.edit().putString(key, sdf1.format(c.getTime())).apply();
		                	t.setText(ssdf.format(c.getTime()));
	                	}
		            }
		        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		        datePickerDialog.show();
		    }
		};
		
		changestart.setOnClickListener(showDatePicker);
		changeend.setOnClickListener(showDatePicker);
	}


}