package com.example.bunkmasterfull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.bunkmasterfull.timetable.Timetable;
import com.example.bunkmasterfull.weekview.BunkOrTempTT;

import android.content.SharedPreferences;
import android.util.Log;

public class Update		//IMPLEMENTED WITHOUT CONSIDERING PERMANENT TIMETABLE CHANGES
{
	DBHelper db;
	SharedPreferences sp;
	Timetable tt;
	String names[];
	final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
	
	public Update(DBHelper dbb, SharedPreferences spref)
	{
		db = dbb;
		sp = spref;
		tt = db.getTimetable();
		names = db.getAllNames();
	}
	
	public int[] getClassCounts(Calendar start, Calendar end)
	{
		int count[] = new int[names.length];
		if(count.length == 0) return count;
		
		for(int i=0; i<count.length; i++) count[i] = 0;
		
		int day = start.get(Calendar.DAY_OF_WEEK);
		switch(day)
		{
		case Calendar.MONDAY: day = 0; break;
		case Calendar.TUESDAY: day=  1; break;
		case Calendar.WEDNESDAY: day = 2; break;
		case Calendar.THURSDAY: day = 3; break;
		case Calendar.FRIDAY: day = 4; break;
		case Calendar.SATURDAY: day = 5; break;
		default: day = 6; break;
		}
		
		//iterate over the days
		for(; beforeOrEqual(start, end); start.add(Calendar.DAY_OF_MONTH, 1), day = (day+1) % 7)
		{
			if(day>5) continue;
			
			for(int i=0; i<6; i++)  //iterate over the hours of "it"'s day
			{
				for(int j=0; j<names.length; j++)	//find which subject ith hour is
				{
					if(names[j].equals(tt.getHour(day, i)))
						count[j]++;
				}
			}
		}
		return count;
	}
	
	public int[] getHeldCount()
	{
		Calendar today = Calendar.getInstance();
		
		String st = sp.getString("semstart", null);	//start date
		Calendar start = Calendar.getInstance();
		try {	start.setTime(sdf.parse(st));	} 
		catch (ParseException e) {	}
		
		int count[] = getClassCounts(start, today);
		BunkOrTempTT[] temptt = db.getAllTempTT();
		
		
		for(int i = 0; i<temptt.length; i++)
		{
			int hour = temptt[i].getHour();
			int day = temptt[i].getDate().get(Calendar.DAY_OF_WEEK);  
			String newhour = temptt[i].getSubject();  //new hour value
			
			switch(day)
			{
			case Calendar.MONDAY: day = 0; break;
			case Calendar.TUESDAY: day = 1; break;
			case Calendar.WEDNESDAY: day = 2; break;			//get day on which change is done
			case Calendar.THURSDAY: day = 3; break;
			case Calendar.FRIDAY: day = 4; break;
			case Calendar.SATURDAY: day = 5; break;
			default: day = 6;
			}
			
			String oldhour = tt.getHour(day, hour);	//old hour value
			int old=-1, nw=-1;
			for(int j=0; j<names.length; j++)
			{
				if(oldhour.equals(names[j])) old = j;
				if(newhour.equals(names[j])) nw = j;
			}
			
			if(old>=0)	count[old]--;
			if(nw>=0)	count[nw]++;
		}
		
		return count;
	}
	
	public void updateExpected()		
	{
		String st = sp.getString("semstart", null);	//start date
		String en = sp.getString("semend", null);		//end date
		
		Calendar start = Calendar.getInstance();
		try {	start.setTime(sdf.parse(st));	} 
		catch (ParseException e) {	}
		
		Calendar end = Calendar.getInstance();
		try {	end.setTime(sdf.parse(en));	} 
		catch (ParseException e) {	}
		
		int []count = getClassCounts(start, end);
		//all subject's counts obtained. Now update DB
		
		for(int i=0; i<names.length; i++)
			db.updateExpected(names[i], count[i]);
	}
	
	public void updateHeld()
	{
		int count[] = getHeldCount();
		
		for(int i=0; i<names.length; i++)
			db.setHeld(names[i], count[i]);
	}
	
	public void updateAttended()
	{
		int count[] = getHeldCount();
		int held[] = count.clone();
		
		BunkOrTempTT [] bunks = db.getAllBunks();
		
		int val = -1;
		for(int i=0; i<bunks.length; i++)
		{		
			val = -1;
			for(int j =0; j<names.length; j++)
			{
				if(names[j].equals(bunks[i].getSubject()))
				{	val = j;	break;	}
			}
			if(val != -1)
				count[val]--;
		}
		
		
		for(int i=0; i<names.length; i++)
			db.setAttended(names[i], count[i]);
		
		updateSafeBunks(held, count);
		
	}
	
	public void updateSafeBunks(int[] held, int[] attended)
	{
		float crit = (float) Integer.parseInt(sp.getString("criteria", "75"));
		for(int i=0; i<held.length; i++)
		{
			int h = held[i];
			if(h == 0) continue;
			int a = attended[i];
			
			float per = ( (float) a  /  (float) h )  * 100;
			int safe = 0;
			
			if(per < crit)		//calculate classes to attend to cross criteria
			{
				while(true)
				{
					float tempper = ( (float) (a+safe)  /  (float) (h+safe) )  * 100;
					if(tempper > crit) break;
					safe++;
				}
				db.setSafeBunks(names[i], (-1 * safe));
			}
			
			else			//calculate safe bunks
			{
				while(true)
				{
					float tempper = ( (float) a  /  (float) (h+safe) )  * 100;
					if(tempper <= crit) break;
					safe++;
				}
				db.setSafeBunks(names[i], safe);
			}
		}
	}
	
	public void update()
	{
		updateHeld();
		updateAttended();	//updateAttended also updates safe bunks
	}
	
	public boolean beforeOrEqual(Calendar c1, Calendar c2)
	{
		if(c1.before(c2) || isSameDate(c1, c2))
			return true;
		
		return false;
	}
	
	public boolean isSameDate(Calendar c1, Calendar c2)
	{
		if(c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
							&& c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
			return true;
			
		else return false;
	}
	
	
	
	
	
	
	
	//BETTER IMPLEMENTATION THAN ^THAT SHIT
	//But tedious as hell :/
	//Lots of room for errors
	//pls fix :'(
	
	public void initAll()
	{
		updateExpected();
		
		updateHeld();
		
		updateAttended();
	}
	
	public void dailyUpdate(Calendar cal)
	{
		//Calendar cal = Calendar.getInstance();	
		int day = cal.get(Calendar.DAY_OF_WEEK) - 2;  //sunday is 1
		
		final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		Log.i("TODAY", "The date is: "+sdf.format(cal.getTime()));
		if(day<0) return;
		
		for(int hour=0; hour<6; hour++)
		{
			//check if hour is present in temptt. If not, increment the ttsub
			//if present, do nothing because trigger has already taken care
			
			if(!db.hourOfDateExistsInTempTT(cal, hour))
			{
				String ttsub = tt.getHour(day, hour);
				db.incrementHeld(ttsub);
				Log.i("DAILYUPDATE", "hour: "+hour+". Subject '"+ttsub+"' incremented. Day is: "+day);
			}
		}
		
		sp.edit().putString("lastcalculated", sdf.format(cal.getTime())).apply();
	}	
}