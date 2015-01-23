package com.example.bunkmasterfull.options;

import com.example.bunkmasterfull.R;
import com.example.bunkmasterfull.R.id;
import com.example.bunkmasterfull.R.layout;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class Help extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		TextView tv=(TextView) findViewById(R.id.help);
		
		String text = "Enter your subjects first: You cannot change your subjects later. Try using short forms so it's easy" +
				" to find your subjects in the Timetable and Week View.\n\n"
					+"Enter your timetable. Be careful while doing this, don't make any errors. Do not change it later.\n\n"+
				"Now you are ready to start bunking! Click on the Week View to view all your classes in a week format." +
				"If a class is cancelled or if a different lecture is held, just tap that hour and choose 'free' or the new subject.\n" +
				"All classes held till date are in green (meaning present). Long-click on any hour to mark yourself absent, it's " +
				"as simple as that!\n\n	Now that you've bunked a few classes, let's see your attendance. Click on Subjects from th" +
				"e main screen. Click on any subject to view its current status. \n\nYou're all set!";
		
		String stext = "Bunkmaster is the ultimate attendance management application! With Bunkmaster, you can happily bunk classes" +
				" without a worry! Bunkmaster will tell you when you can and can't bunk! Here are the parts of the application" +
				" explained:<br><br><u>SUBJECTS</u><br>Here you can see a list of all your subjects. You can click on any subject " +
				"to see its status. You can also add new subjects.<br><br><u>TIMETABLE</u><br>You can enter your timetable here. Once" +
				" you enter it, don't change it later, unless you made a mistake or forgot something.<br><br><u>WEEK VIEW</u><br>Here " +
				"you can see your class schedule in a week format. All subjects are marked 'present' by default. If" +
				" you want to mark yourself absent, just long-click on the hour. In case there is a different subject or the" +
				" class is cancelled, click on the hour and select the changed subject or just click 'free'.<br><br>" +
				"<u>IMPORTANT DATES</u><br>This displays a list of all the important dates in your semester. An important date is any " +
				"event in your semester, like an exam or some day you don't want to miss, or WANT to miss ;) You can " +
				"choose to have a reminder for an event. You can also add events here.<br><br><u>SEMESTER DATES</u><br>Here you " +
				"can set your semester start date and end date.<br><br>Happy bunking!<br>";
		
		tv.setText(Html.fromHtml(stext));
		
		Typeface type = Typeface.createFromAsset(getAssets(),"Good Dog Cool.TTF");
		tv.setTypeface(type);
		tv.setTextSize(32);
	}
}
