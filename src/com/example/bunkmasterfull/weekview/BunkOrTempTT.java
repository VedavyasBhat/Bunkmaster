package com.example.bunkmasterfull.weekview;
import java.util.Calendar;

public class BunkOrTempTT 
{
	String subject;
	Calendar date;
	int hour;

	BunkOrTempTT() 
	{
		subject = null;
		date = null;
		hour = 0;
	}
	
	BunkOrTempTT(String s) 
	{
		subject = s;
		date = null;
		hour = 0;
	}

	public BunkOrTempTT(String s, Calendar d, int h) 
	{
		subject = s;
		date = d;
		hour = h;
	}

	//getters
	public String getSubject() 
	{	return subject;	}
	
	public Calendar getDate() 
	{	return date;	}
	
	public int getHour() 
	{	return hour;	}
	
	//setters
	public void setSubject(String sub) 
	{	subject = sub;	}
	
	public void setDate(Calendar d) 
	{	date = d;	}
	
	public void setHour(int h) 
	{	hour = h;	}
	
}