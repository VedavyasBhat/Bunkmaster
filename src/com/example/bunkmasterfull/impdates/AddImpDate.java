package com.example.bunkmasterfull.impdates;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

public class AddImpDate extends Activity 
{
	Button add;
	EditText name, desc;
	TextView datetv;
	String date;
	CheckBox alarmcheck;
	SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_imp_date);
		// Show the Up button in the action bar.
		setupActionBar();
		
		initVariables();
		initDateTextView();
		initAlarmCheck();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() 
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		name = (EditText) findViewById(R.id.addname);
		desc = (EditText) findViewById(R.id.adddesc);
		datetv = (TextView) findViewById(R.id.date);
		alarmcheck = (CheckBox) findViewById(R.id.alarmcheck);
	}
	
	public void initDateTextView()
	{
		Calendar cal = Calendar.getInstance();
		datetv.setText("Date:    "+sdf.format(cal.getTime()));
		View.OnClickListener showDatePicker = new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Calendar cal = Calendar.getInstance();
				
				DatePickerDialog datePickerDialog = new DatePickerDialog(AddImpDate.this, new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker arg0, int year, int month, int day) 
					{
						Calendar c = Calendar.getInstance();
	                	c.set(year, month, day);
	                	date = sdf.format(c.getTime());
	                	datetv.setText("Date:    "+sdf.format(c.getTime()));
					}
					
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
			}
		};
		
		datetv.setClickable(true);
		datetv.setOnClickListener(showDatePicker);
	}
	
	public void initAlarmCheck()
	{
		alarmcheck.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				if( ((CheckBox) view).isChecked() )
				{
					LayoutInflater li = LayoutInflater.from(AddImpDate.this);
					View v = li.inflate(R.layout.imp_date_alarm_dialog, null);
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(AddImpDate.this);
					dialog.setTitle("Delete subject");
					dialog.setView(v);
					
				}
			}
			
		});
	}
	
	public void done(View view)
	{
		DBHelper db = new DBHelper(this, null, null, 1);
		if(date==null)
			date=sdf.format(Calendar.getInstance().getTime());
		
		String n = name.getText().toString().trim(); 
		if( n.equals("") || n == null)
		{
			noNameEntered();
			db.close();
			return;
		}
		
		db.addImpDate(n, date, desc.getText().toString(), alarmcheck.isChecked());
		db.close();
		finish();
	}
	
	public void noNameEntered()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("No name entered");
		dialog.setMessage("You have not entered a name for this event! Please enter one.");
		dialog.setCancelable(false);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dlg, int which) 
			{
				dlg.cancel();
			}
		});
		
		AlertDialog al = dialog.create();
		al.show();
	}
}