package com.example.bunkmasterfull.weekview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunkmasterfull.DBHelper;
import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.options.Help;
import com.example.bunkmasterfull.options.SettingsActivity;
import com.example.bunkmasterfull.timetable.Timetable;

public class WeekView extends Activity 
{
	Spinner hours[][]=new Spinner[6][6];
	TextView weekdates, mon, tue, wed, thu, fri, sat;
	ArrayAdapter<String> adapter;
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
	Calendar semstart, semend;
	Button previous, next;
	boolean animval;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_view);
		// Show the Up button in the action bar.
		setupActionBar();
		
		initVariables();
		
		getSemDates();
		
		setVariablesValues();
		
		Calendar today = Calendar.getInstance();
		setListeners();
		setWeekDates(today);
		setNames(today);
		setColoursOfButtons(today);
	}
	
	public void getSemDates()
	{
		SharedPreferences semdates = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String semst = semdates.getString("semstart", "01-1-2013");
		String semen = semdates.getString("semend", "01-1-2014");
		
		SimpleDateFormat ssdf = new SimpleDateFormat("dd-M-yyyy");
		
		semstart = Calendar.getInstance();
        try {	semstart.setTime(ssdf.parse(semst));	}
        catch (ParseException e) {e.printStackTrace();	}
        
        semend = Calendar.getInstance();
        try {	semend.setTime(ssdf.parse(semen));	}
        catch (ParseException e) {e.printStackTrace();	}
        
        if(semstart.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 
        	semstart.add(Calendar.DAY_OF_MONTH, 1);
        
        if(semend.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 
        	semend.add(Calendar.DAY_OF_MONTH, -1);
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
	
	public void setListeners()
	{
		OnItemSelectedListener oisl = new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
			{
				int index = Integer.parseInt((String) ((Spinner) parentView).getPrompt());
				int i = index / 10;
				int j = index % 10;
				
				//get the date of selected hour
				String date = (String) (weekdates.getText()).subSequence(0, 11);
				Calendar cal = Calendar.getInstance();
				try 
				{
					cal.setTime(sdf.parse(date));
				} catch (ParseException e) {}
				cal.add(Calendar.DATE, i);	//add the spinner's day number to date
				
				DBHelper db=new DBHelper(WeekView.this, null, null, 1); 
				String newsub = new String(adapter.getItem(position));
				
				int col = db.getColour(newsub);
				if(col == 0) col = 0xFF000000;
				
				if(adapter.getItem(position) != "free")
					((TextView) parentView.getSelectedView()).setTypeface(Typeface.DEFAULT_BOLD);
				else												//setting the typeface of text
					((TextView) parentView.getSelectedView()).setTypeface(Typeface.DEFAULT);
				
				((TextView) parentView.getSelectedView()).setTextColor(col);		//setting the colour of text
				
				if((Integer) hours[i][j].getTag(R.id.pos) != position)		//if item is selected by user and not set by me
				{	
					BunkOrTempTT newtemporbunk = new BunkOrTempTT(newsub, cal, j);
					db.addTempTT(newtemporbunk);		//insert new temptt

					String oldsub = new String(adapter.getItem((Integer)hours[i][j].getTag(R.id.pos))); //old subject
					BunkOrTempTT oldtemporbunk = new BunkOrTempTT(oldsub, cal, j);
					db.removeTempTT(oldtemporbunk);		//delete old temptt
					
					if(position == 0) //if hour is changed to free, colour it gray
					{
						//if the hour was marked absent, remove the old bunk
						if((Integer) hours[i][j].getTag(R.id.color) == Color.RED) //if hour Was marked bunked, remove old bunk ONLY
							db.removeBunk(oldtemporbunk); //removing old bunk
						
						hours[i][j].setBackgroundColor(Color.LTGRAY);
						hours[i][j].setTag(R.id.color, Color.LTGRAY);
						
						showAwwYeahMeme();
					}
					
					else if(position != 0)		//changed to something besides free
					{
						if((Integer) hours[i][j].getTag(R.id.color) == Color.RED) //if hour is already bunked, remove old bunk and
						{																							//add new bunk
							db.removeBunk(oldtemporbunk); //removing old bunk
							db.addBunk(newtemporbunk);  //adding new bunk (which is also the temptt just entered, btw)
						}
						
						if((Integer) hours[i][j].getTag(R.id.color) == Color.LTGRAY)  //if hour was free
						{
							hours[i][j].setBackgroundColor(Color.GREEN);
							hours[i][j].setTag(R.id.color, Color.GREEN);
							
							if((Integer) hours[i][j].getTag(R.id.pos) == 0)
								showFuuuMeme();
						}
					}
				}
				hours[i][j].setTag(R.id.pos, position);		//setting new position
				db.close();
			}
			
			public void showAwwYeahMeme()
			{
				ImageView view = new ImageView(WeekView.this); 
			    view.setImageResource(R.drawable.awwyeah);
			    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			    view.setLayoutParams(lp);
			    view.setAdjustViewBounds(true);
			    view.setMaxHeight(130);
			    view.setMaxWidth(130);
			    Toast toast = new Toast(WeekView.this);
			    toast.setView(view);
			    toast.show();
			}
			
			public void showFuuuMeme()
			{
				ImageView view = new ImageView(WeekView.this); 
			    view.setImageResource(R.drawable.fuuu);
			    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			    view.setLayoutParams(lp);
			    view.setAdjustViewBounds(true);
			    view.setMaxHeight(130);
			    view.setMaxWidth(130);
			    Toast toast = new Toast(WeekView.this);
			    toast.setView(view);
			    toast.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{}
		};
		
		OnLongClickListener olcl = new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view) 
			{
				if((Integer) view.getTag(R.id.color) != Color.LTGRAY)
				{
					int index = Integer.parseInt((String) ((Spinner) view).getPrompt());
					int i = index / 10;
					int j = index % 10;
					
					int currentcolour = (Integer) hours[i][j].getTag(R.id.color);
					
					String sub = new String((String) hours[i][j].getSelectedItem());
					
					String date = (String) (weekdates.getText()).subSequence(0, 11);
					Calendar cal = Calendar.getInstance();
					try 
					{
						cal.setTime(sdf.parse(date));
					} catch (ParseException e) {}
					
					cal.add(Calendar.DATE, i);	//add the spinner's day number to date
					
					BunkOrTempTT bunk = new BunkOrTempTT(sub, cal, j); //create the bunk object to be inserted/deleted from db 
					DBHelper db=new DBHelper(WeekView.this, null, null, 1); 
					
					
					if(currentcolour == Color.RED)
					{
						hours[i][j].setBackgroundColor(Color.GREEN);
						hours[i][j].setTag(R.id.color, Color.GREEN);
						db.removeBunk(bunk);
						
						showFuuuMeme();
					}
					
					if(currentcolour == Color.GREEN)
					{
						hours[i][j].setBackgroundColor(Color.RED);
						hours[i][j].setTag(R.id.color, Color.RED);
						db.addBunk(bunk);
						
						showLikeabaussMeme();
					}
					db.close();
				}
				return true;
			}
			
			public void showFuuuMeme()
			{
				ImageView view = new ImageView(WeekView.this); 
			    view.setImageResource(R.drawable.fuuu);
			    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			    view.setLayoutParams(lp);
			    view.setAdjustViewBounds(true);
			    view.setMaxHeight(150);
			    view.setMaxWidth(150);
			    Toast toast = new Toast(WeekView.this);
			    toast.setView(view);
			    toast.show();
			}
			
			public void showLikeabaussMeme()
			{
				ImageView view = new ImageView(WeekView.this); 
			    view.setImageResource(R.drawable.likeabauss);
			    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			    view.setLayoutParams(lp);
			    view.setAdjustViewBounds(true);
			    view.setMaxHeight(130);
			    view.setMaxWidth(130);
			    Toast toast = new Toast(WeekView.this);
			    toast.setView(view);
			    toast.show();
			}
			
		};
		
		
		for(int i=0; i<6; i++)
		{
			for(int j=0; j<6; j++)
			{
				hours[i][j].setOnItemSelectedListener(oisl);
				hours[i][j].setOnLongClickListener(olcl);
			}
		}
	}
	
	public void setNames(Calendar cal)
	{
		DBHelper db=new DBHelper(this, null, null, 1);
		
		BunkOrTempTT temptt[] = db.getAllTempTT();
		Timetable tt = db.getTimetable();
		
		Calendar now = (Calendar) cal.clone();
		long time = cal.getTimeInMillis();
		now.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); //Sunday because 1 gets added in the first exec of for loop
		if(now.getTimeInMillis() > time) now.add(Calendar.DATE, -7);
		
		Calendar today = Calendar.getInstance();
		boolean flag;
		
		int pos;
		for(int i=0; i<6; i++)
		{
			now.add(Calendar.DAY_OF_MONTH, 1);
			
			/*if(today.before(now))
			{									//disable clicking if day is after today.
				for(int j=0; j<6; j++)			//necessary to do current implementation of Attendance calc.
					hours[i][j].setEnabled(false);
				continue;
			}*/
			
			if((now.before(semstart) || now.after(semend)) && (!isSameDate(now, semstart) || !isSameDate(now, semend)))
			{				//if the date is out of the semester
				for(int j=0; j<6; j++)				//make all hours invisible
					hours[i][j].setVisibility(View.GONE);
				
				//make the label invisible
				switch(i)
				{
				case 0: mon.setVisibility(View.INVISIBLE); break;
				case 1: tue.setVisibility(View.INVISIBLE); break;
				case 2: wed.setVisibility(View.INVISIBLE); break;
				case 3: thu.setVisibility(View.INVISIBLE); break;
				case 4: fri.setVisibility(View.INVISIBLE); break;
				case 5: sat.setVisibility(View.INVISIBLE); break;
				}
				continue;
			}
			else
			{
				//make the label visible in case it isn't
				switch(i)
				{
				case 0: mon.setVisibility(View.VISIBLE); break;
				case 1: tue.setVisibility(View.VISIBLE); break;
				case 2: wed.setVisibility(View.VISIBLE); break;
				case 3: thu.setVisibility(View.VISIBLE); break;
				case 4: fri.setVisibility(View.VISIBLE); break;
				case 5: sat.setVisibility(View.VISIBLE); break;
				}
				
			}
			
			for(int j=0; j<6; j++)				//put selected items into buttons
			{
				hours[i][j].setVisibility(View.VISIBLE);
				//hours[i][j].setEnabled(true);
				
				flag = true;
				for(int k=0; k<temptt.length; k++)
				{	
					if(isSameDate(now, temptt[k].getDate()) && temptt[k].getHour()==j) //if this hour is present in temptt
					{
						pos=adapter.getPosition(temptt[k].getSubject()); //get position of this subject from the adapter
						hours[i][j].setSelection(pos);  //set the selection as the above one
						hours[i][j].setTag(R.id.pos, pos);
						
						flag = false;
						break;
					}
				}
				if(flag)  //if hour is not changed
				{
					if(hours[i][j].getSelectedItem() != tt.getHour(i, j)) //if hour is not the same as from tt
					{														//change to the tt hour
						pos = adapter.getPosition(tt.getHour(i, j));
						hours[i][j].setSelection(pos);
						hours[i][j].setTag(R.id.pos, pos);
					}
				}
			}
		}
		db.close();
	}
	
	public void setColoursOfButtons(Calendar cal) //set colour based on current date and bunks
	{
		//if date is greater than current, colour is grey
		//if not, then check db. if this hour (with this subject?? to avoid db inconsistencies) present in db, red. else green.
			
		DBHelper db=new DBHelper(this, null, null, 1);
		
		BunkOrTempTT bunks[] = db.getAllBunks();
		
		Calendar now = (Calendar) cal.clone();
		long time = now.getTimeInMillis();
		now.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		if(now.getTimeInMillis() > time) now.add(Calendar.DATE, -7);
		
		Calendar today = Calendar.getInstance();
		
		boolean flag;
				
		for(int i=0; i<6; i++)
		{
			now.add(Calendar.DATE, 1);
			
			if((now.before(semstart) || now.after(semend)) && !isSameDate(now, semstart) && !isSameDate(now, semend))
				continue;
			
			if(now.after(today)) //if the date to be coloured is after today
			{
				//colour the whole day grey
				for(int j=0; j<6; j++)
				{
					hours[i][j].setBackgroundColor(Color.LTGRAY);
					hours[i][j].setTag(R.id.color, Color.LTGRAY);
				}
				continue;
			}
			
			for(int j=0; j<6; j++)
			{
				//in day i, consider all hours j
				if((String) hours[i][j].getSelectedItem() == "free" )
				{
					hours[i][j].setBackgroundColor(Color.LTGRAY);
					hours[i][j].setTag(R.id.color, Color.LTGRAY);
					continue;
				}
				flag = true;
				for(int k=0; k<bunks.length; k++)	//if hour j of day i is present in bunks
				{
					if(isSameDate(now, bunks[k].getDate()) && bunks[k].getHour()==j) //if this hour is marked absent according to the DB
					{ 
						hours[i][j].setBackgroundColor(Color.RED);
						hours[i][j].setTag(R.id.color, Color.RED);
						
						flag = false;
						break;
					}
				}
				if(flag) 
				{
					hours[i][j].setBackgroundColor(Color.GREEN);
					hours[i][j].setTag(R.id.color, Color.GREEN);
				}
			}
		}
		db.close(); 
	}
	
	public void setWeekDates(Calendar cal)
	{
		Calendar start = (Calendar) cal.clone();
		start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		if(start.after(cal) && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) start.add(Calendar.DATE, -7);  //fix to take care of date inconsistency on different systems
		
		Calendar end = (Calendar) cal.clone();
		end.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		if(end.before(cal) && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) end.add(Calendar.DATE, 7);
		
        if(semstart.after(start) || isSameDate(semstart, start))
        {
        	start = (Calendar) semstart.clone();
        	previous.setVisibility(Button.INVISIBLE);
        }
        else previous.setVisibility(Button.VISIBLE);
		
        if(semend.before(end) || isSameDate(semend, end))
        {
        	end = (Calendar) semend.clone();
        	next.setVisibility(Button.INVISIBLE);
        }
        else next.setVisibility(Button.VISIBLE);
        
		String dates = sdf.format(start.getTime()) + " to " + sdf.format(end.getTime());
		weekdates.setText(dates);
	}
	
	public void loadPrevious(View view)
	{		
		String date = (String) (weekdates.getText()).subSequence(0, 11);
		Calendar cal = Calendar.getInstance();
		try 
		{
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {}
		
		cal.add(Calendar.DATE, -4);
		
		
		animateRightOutLeftIn(cal);
	}
	
	public void loadNext(View view)
	{		
		String date = (String) (weekdates.getText()).subSequence(15, 26);
		Calendar cal = Calendar.getInstance();
		try 
		{
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {}
		
		cal.add(Calendar.DATE, 4);
		
		animateLeftOutRightIn(cal);
	}
	
	public void animateRightOutLeftIn(Calendar cal)
	{
		View v = this.findViewById(R.id.blergh);
		final Calendar c = cal;
		Animation anim1 = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
		anim1.setDuration(600);
		anim1.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				View v = WeekView.this.findViewById(R.id.blergh);
				Animation anim1 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
				anim1.setDuration(750);
				v.startAnimation(anim1);
				
				setWeekDates(c);
				setNames(c);
				setColoursOfButtons(c);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) 
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation arg0) 
			{
				// TODO Auto-generated method stub
			}
			
		});
		v.startAnimation(anim1);
	}
	
	public void animateLeftOutRightIn(Calendar cal)
	{
		View v = this.findViewById(R.id.blergh);
		final Calendar c = cal;
		Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		anim1.setDuration(600);
		anim1.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) 
			{
				View v = WeekView.this.findViewById(R.id.blergh);
				Animation anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
				anim1.setDuration(750);
				v.startAnimation(anim1);
				
				setWeekDates(c);
				setNames(c);
				setColoursOfButtons(c);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) 
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation arg0) 
			{
				// TODO Auto-generated method stub
			}
			
		});
		v.startAnimation(anim1);
	}
	
	public boolean isSameDate(Calendar c1, Calendar c2)
	{
		if(c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
							&& c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
		{
			return true;
		}
		else return false;
	}
	
	public void initVariables()
	{
		hours[0][0]=(Spinner)findViewById(R.id.mon1);	hours[3][0]=(Spinner)findViewById(R.id.thu1);
		hours[0][1]=(Spinner)findViewById(R.id.mon2);	hours[3][1]=(Spinner)findViewById(R.id.thu2);
		hours[0][2]=(Spinner)findViewById(R.id.mon3);	hours[3][2]=(Spinner)findViewById(R.id.thu3);
		hours[0][3]=(Spinner)findViewById(R.id.mon4);	hours[3][3]=(Spinner)findViewById(R.id.thu4);
		hours[0][4]=(Spinner)findViewById(R.id.mon5);	hours[3][4]=(Spinner)findViewById(R.id.thu5);
		hours[0][5]=(Spinner)findViewById(R.id.mon6);	hours[3][5]=(Spinner)findViewById(R.id.thu6);
		hours[1][0]=(Spinner)findViewById(R.id.tue1);	hours[4][0]=(Spinner)findViewById(R.id.fri1);
		hours[1][1]=(Spinner)findViewById(R.id.tue2);	hours[4][1]=(Spinner)findViewById(R.id.fri2);
		hours[1][2]=(Spinner)findViewById(R.id.tue3);	hours[4][2]=(Spinner)findViewById(R.id.fri3);
		hours[1][3]=(Spinner)findViewById(R.id.tue4);	hours[4][3]=(Spinner)findViewById(R.id.fri4);
		hours[1][4]=(Spinner)findViewById(R.id.tue5);	hours[4][4]=(Spinner)findViewById(R.id.fri5);
		hours[1][5]=(Spinner)findViewById(R.id.tue6);	hours[4][5]=(Spinner)findViewById(R.id.fri6);
		hours[2][0]=(Spinner)findViewById(R.id.wed1);	hours[5][0]=(Spinner)findViewById(R.id.sat1);
		hours[2][1]=(Spinner)findViewById(R.id.wed2);	hours[5][1]=(Spinner)findViewById(R.id.sat2);
		hours[2][2]=(Spinner)findViewById(R.id.wed3);	hours[5][2]=(Spinner)findViewById(R.id.sat3);
		hours[2][3]=(Spinner)findViewById(R.id.wed4);	hours[5][3]=(Spinner)findViewById(R.id.sat4);
		hours[2][4]=(Spinner)findViewById(R.id.wed5);	hours[5][4]=(Spinner)findViewById(R.id.sat5);
		hours[2][5]=(Spinner)findViewById(R.id.wed6);	hours[5][5]=(Spinner)findViewById(R.id.sat6);
		
		previous = (Button) findViewById(R.id.previous);
		next = (Button) findViewById(R.id.next);
		weekdates = (TextView) findViewById(R.id.weekdates);
		
		mon = (TextView) findViewById(R.id.wvmon);
		tue = (TextView) findViewById(R.id.wvtue);
		wed = (TextView) findViewById(R.id.wvwed);
		thu = (TextView) findViewById(R.id.wvthu);
		fri = (TextView) findViewById(R.id.wvfri);
		sat = (TextView) findViewById(R.id.wvsat);		
	}
	
	public void setVariablesValues()
	{
		for(int i=0; i<6; i++)			//set the i and j values to each Spinner
			for(int j=0; j<6; j++)
				hours[i][j].setPrompt(String.valueOf(i*10 + j));

		DBHelper db=new DBHelper(this, null, null, 1);
		String n[]=db.getAllNames();
		String names[]=new String[n.length + 1];
		names[0]="free";
		for(int i=0; i<n.length; i++)
			names[i+1]=n[i];
		
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);  //initialize the adapter to be used for
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);			//all the spinners
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)				//setting the same adapter for all spinners
				hours[i][j].setAdapter(adapter);		
	}
}