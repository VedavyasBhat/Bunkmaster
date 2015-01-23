package com.example.bunkmasterfull.subjects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;

public class ShowSubjects extends Activity 
 {
	String names[];
	SubjectsArrayAdapter adapter;
	GridView grid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_subjects);
		
		grid = (GridView) findViewById(R.id.grid);
		refreshNames();
		adapter = new SubjectsArrayAdapter(this, names);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) 
			{
				String sub = (String) parent.getItemAtPosition(position);
				Intent intent=new Intent(ShowSubjects.this, SubjectStatus.class);
				intent.putExtra("NAME", sub);		//passing subject name to SubjectStatus activity
				startActivity(intent);
				
			}
		});
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
		refreshNames();
		adapter = new SubjectsArrayAdapter(this, names);
		//setListAdapter(adapter);
	}		
	
	public void refreshNames()
	{
		DBHelper db = new DBHelper(this, null, null, 1);
		names = db.getAllNames();
		db.close();
	}
	
	public void addSubject(View view)
	{
		Intent intent=new Intent(this, AddSubject.class);
		startActivity(intent);
	}
}