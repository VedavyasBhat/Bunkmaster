package com.example.bunkmasterfull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.bunkmasterfull.impdates.ImpDateStatus;
import com.example.bunkmasterfull.subjects.SubjectStatus;


//This service does three things:
//1. Updates attendance everyday at 7:00pm
//2. Makes notifs if attendance is low
//3. Makes notifs if imp date is coming



@SuppressLint("NewApi")
public class Monitor extends IntentService
{
	DBHelper db;
	public Monitor()
	{
		super("Attendance Monitor");
	}
	
	public Monitor(String name) 
	{
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		//get the attendance of all subjects
		//if below criteria, make a notification
		
		//fetch all important dates
		//for each date, set an alarm two days before the date(provided that date is after today)

		db=new DBHelper(this, null, null, 1);
		
		switch(intent.getIntExtra("service", -1))
		{
		case 0:	updateAttendance(); checkAttendance(); break;
		case 1: checkImpDates(); break;
		case 2: checkAttendance(); break;
		default: break;
		}
		db.close();
	}
	
	public void checkAttendance()
	{
		String [] subjects = db.getAllNames();
		
		for(int i=0; i<subjects.length; i++)
		{
			int safe = db.getSafeBunks(subjects[i]);
			if(safe < 0)
				notifyLowAttendance(subjects[i]);
		}
	}
	
	public void checkImpDates()
	{
		String[][] impdates = db.getAllImpDates();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		for(int i=0; i<impdates.length; i++)
		{
			Calendar cal = Calendar.getInstance();
			Calendar today = Calendar.getInstance();
			try 
			{
				cal.setTime(sdf.parse(impdates[i][1]));
			} catch (ParseException e) {}
			
			cal.add(Calendar.DAY_OF_MONTH, -1);
			if(isSameDate(today, cal)) 
			{
				Handler mandler = new Handler(getMainLooper());
			    mandler.post(new Runnable() {
			        @Override
			        public void run() {
			        	Toast.makeText(getApplicationContext(), "TOMORROW!", Toast.LENGTH_LONG).show();
			        }
			    });
				notifyImpDate(impdates[i], true);	//true is for tomorrow
			}
			
			cal.add(Calendar.DAY_OF_MONTH, -1);
			if(isSameDate(today, cal)) notifyImpDate(impdates[i], false);	//false is for day after
		}
	}
	
	public void updateAttendance()
	{
		/*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Update update = new Update(db, sp);
		update.dailyUpdate();		*/
	}
	
	public void notifyLowAttendance(String subject)
	{	    
	    float per = ( (float) db.getAttended(subject) / (float) db.getHeld(subject) ) * 100;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		
		builder.setSmallIcon(R.drawable.cry);
		
		
		
		builder.setContentTitle("Low attendance!");
		builder.setContentText("Attendance in "+subject+" is low ("+new DecimalFormat("##.##").format(per)+"%)!");
		
		Intent resultIntent = new Intent(this, SubjectStatus.class);
		resultIntent.putExtra("NAME", subject);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(SubjectStatus.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		
		NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		man.notify(subject.hashCode(), builder.build());
	}
	
	public void notifyImpDate(String [] impdate, boolean day)
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(android.R.drawable.toast_frame);		//change to some drawable
		builder.setContentTitle("Event coming up!");
		
		String daay;
		if(day) daay = "tomorrow";
		else daay = "day after tomorrow";
		
		builder.setContentText("Watch out! "+impdate[0]+" is coming up "+daay+". Do not miss class that day.");
		
		Intent resultIntent = new Intent(this, ImpDateStatus.class);
		resultIntent.putExtra("impdate", impdate[0]);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(ImpDateStatus.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		
		NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		man.notify(impdate[0].hashCode(), builder.build());
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
}