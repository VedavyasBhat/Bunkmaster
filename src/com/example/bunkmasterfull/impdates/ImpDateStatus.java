package com.example.bunkmasterfull.impdates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

public class ImpDateStatus extends Activity 
{
	TextView name, date, desc; 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.impdate_status);
		// Show the Up button in the action bar.
		setupActionBar();
		
		name = (TextView) findViewById(R.id.impdatename);
		date = (TextView) findViewById(R.id.impdatedate);
		desc = (TextView) findViewById(R.id.impdatedesc);
		
		DBHelper db=new DBHelper(this, null, null, 1);
		String nam = getIntent().getStringExtra("impdate");
		String [] impdates = db.getImpDateDetails(nam);
		db.close();
		
		name.setText(impdates[0]);
		date.setText(impdates[1]);
		desc.setText(impdates[2]);
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

	public void deleteImpDate(View view)
	{
		final String impdatename = (String) name.getText();
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Delete important date");
		dialog.setMessage("Are you sure you want to delete this event? This action cannot be undone.");
		dialog.setCancelable(true);
		dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				DBHelper db=new DBHelper(ImpDateStatus.this, null, null, 1);
				db.deleteImpDate(impdatename);
				Toast.makeText(getApplicationContext(), "Event deleted!", Toast.LENGTH_SHORT).show();
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
}