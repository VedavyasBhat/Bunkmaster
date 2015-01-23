package com.example.bunkmasterfull.timetable;

public class Timetable 
{	
	private String timetable[][];
	
	public Timetable()
	{
		timetable = new String[6][6];
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				timetable[i][j] = "free";
	}
	
	public String[][] getTimetable()
	{
		return timetable;
	}
	
	public void setHour(int day, int hour, String subject)
	{
		timetable[day][hour] = subject;
	}
	
	public String getHour(int day, int hour)
	{
		return timetable[day][hour];
	}
}