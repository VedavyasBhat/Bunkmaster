package com.example.bunkmasterfull.subjects;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

public class SubjectStatus extends Activity 
{
	String name;
	TextView subname, subexpected, subheld, subattended, subpercent, subsafebunks;
	Spinner subcolour;
	CheckBox check;
	PieChart pi;
	String [] colours;
	int [] colint;
	ArrayAdapter<String> adapter;
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject_status);
		// Show the Up button in the action bar.
		setupActionBar();
		
		initVariables();
		setVariableValues();
		
		
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
		subname = (TextView)findViewById(R.id.subname);
		subcolour = (Spinner)findViewById(R.id.subcolourspin);
		subexpected = (TextView)findViewById(R.id.subexpected);
		subheld = (TextView)findViewById(R.id.subheld);
		subattended = (TextView)findViewById(R.id.subattended);
		subpercent = (TextView)findViewById(R.id.subpercent);
		subsafebunks = (TextView)findViewById(R.id.subsafebunks);
		pi = (PieChart) findViewById(R.id.subjectpi);
		check = (CheckBox) findViewById(R.id.subnotifcheck);  
		
		colours = new String[] {"Brown", "Chocolate", "Orange", "Dark blue", "Cyan", "Purple", "Yellow", "Bubble gum"};
		colint = new int[] {0xFFA1510B , 0xFF4E2E28, 0xFFFFB41F,  0xFF12169E, 0xFF73FFF6, 0xFFDB14BA, 0xFFF9FF4D, 0xFFED96BC};
								//FF appended before the color to indicate "alpha" value: 00 - transparent, FF - Opaque
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colours);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);			//all the spinners
		subcolour.setAdapter(adapter);
		
		Intent intent=getIntent();
		name=intent.getStringExtra("NAME");
	}
	
	public void deleteSubject(View view)
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Delete subject");
		dialog.setMessage("Are you sure you want to delete this subject? This action cannot be undone.");
		dialog.setCancelable(true);
		dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				DBHelper db=new DBHelper(SubjectStatus.this, null, null, 1);
				db.deleteSubject(name);
				Toast.makeText(getApplicationContext(), "Subject deleted!", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
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
	
	public void setCheckedChangeListener()
	{
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
	        @Override
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	        {
	            //change value of 'alarm' integer in current subject based on isChecked value
	        	DBHelper db = new DBHelper(SubjectStatus.this, null, null, 1);
	        	db.toggleAlarm(name);

	        }
	    });
	}
	
	public void setVariableValues()
	{
DBHelper db=new DBHelper(this, null, null, 1);
		
		int expected=db.getExpected(name);
		int held=db.getHeld(name);
		int attended=db.getAttended(name);
		int safebunks = db.getSafeBunks(name);
		float percent=0;
		if(held!=0) percent=(((float)attended)/ ((float)held)) * 100;
		DecimalFormat f = new DecimalFormat("##.##");
		
		pi.setPercentage(percent);
		
		subname.setText(name);
		subexpected.setText("Expected:   "+expected);
		subheld.setText("Held:   "+String.valueOf(held));
		
		subattended.setText("Attended:   "+String.valueOf(attended));
		subpercent.setText("Percentage:   "+f.format(percent)+"%");
		subsafebunks.setText("Safe bunks:   "+String.valueOf(safebunks));
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		float crit = (float) Integer.parseInt(sp.getString("criteria", "75"));
		if(percent < crit)
		{
			subattended.setTextColor(Color.RED);
			subpercent.setTextColor(Color.RED);
			subsafebunks.setTextColor(Color.RED);
		}
		
		int col = db.getColour(name);
		int i;
		for(i = 0; i<8; i++)
		{
			if(colint[i] == col)
				break;
		}
		
		if(i < 8)
			subcolour.setSelection(adapter.getPosition(colours[i]));
		
		subcolour.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{		//set text colour and add to the DB, then remove from spinner
				String nametext = "<font color=#"+colint[position]+">"+name+"</font>";
				subname.setText(Html.fromHtml(nametext));
				DBHelper db = new DBHelper(SubjectStatus.this, null, null, 1);
				db.changeColour(name, colint[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
			}
			
		});
		
		int danger = db.getSafeBunks(name);
		if(danger < 0)
			Toast.makeText(getApplicationContext(), "Attendance is low! Do not miss another "+(-1*danger)+" classes!", Toast.LENGTH_LONG).show();
		
		setCheckedChangeListener();
	}
}