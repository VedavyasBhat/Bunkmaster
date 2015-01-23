package com.example.bunkmasterfull.impdates;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

public class ShowImpDates extends ListActivity 
{
	String impdates[];
	ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_impdates);
		// Show the Up button in the action bar.
		setupActionBar();
		
		refreshImpDates();
		adapter = new ImpDatesArrayAdapter(this, impdates);
		setListAdapter(adapter);		
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
	
	@Override
	public void onResume()
	{
		super.onResume();
		refreshImpDates();
		adapter = new ImpDatesArrayAdapter(this, impdates);
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		String impdate = (String) getListAdapter().getItem(position);
		Intent intent=new Intent(this, ImpDateStatus.class);
		intent.putExtra("impdate", impdate);		//passing subject name to SubjectStatus activity
		startActivity(intent);
	}
	
	public void refreshImpDates()
	{
		DBHelper db = new DBHelper(this, null, null, 1);
		impdates = db.getAllImpDateNames();
		db.close();
	}
	
	
	public void startAddImportantDate(View view)
	{
		startActivity(new Intent(this, AddImpDate.class));
	}
}