package com.example.bunkmasterfull.subjects;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

public class AddSubject extends Activity 
{
	EditText texts[]=new EditText[4];
	DBHelper db;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subject);
		// Show the Up button in the action bar.
		setupActionBar();
		texts[0] = (EditText) findViewById(R.id.subj1);
		texts[1] = (EditText) findViewById(R.id.subj2);
		texts[2] = (EditText) findViewById(R.id.subj3);
		texts[3] = (EditText) findViewById(R.id.subj4);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
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
	
	public void makeNextVisible(View view) 
	{
		for (int i = 0; i < 4; i++) 
		{
			if (texts[i].getVisibility()==View.VISIBLE && i < 3 && texts[i+1].getVisibility()==View.INVISIBLE)
			{
				texts[i + 1].setVisibility(View.VISIBLE);
				break;
			}
		}
	}
	
	public void insertIntoTable(View view)
	{
		boolean val;
		String name;
		db=new DBHelper(this, null, null, 1);
		String[] names=db.getAllNames();
		for(int i=0; i<4; i++)
		{
			val=true;
			name=texts[i].getText().toString();
			if(!name.equals(("").trim()))
			{
				for(int j=0; j<names.length; j++)
					if(name==names[j]) { val=false; break; }
				
				if(val)
					db.addSubject(name.trim());
			}
		}
		//db.close();
		finish();
	}
}