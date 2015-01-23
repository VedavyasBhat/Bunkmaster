package com.example.bunkmasterfull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.example.bunkmasterfull.timetable.Timetable;
import com.example.bunkmasterfull.weekview.BunkOrTempTT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("SimpleDateFormat")

public class DBHelper extends SQLiteOpenHelper
{
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "bunks.db";
	private static final String TABLE_SUBJECTS = "subjects";
	private static final String TABLE_TIMETABLE = "timetable";
	private static final String TABLE_ABSENT = "absent";
	private static final String TABLE_TEMP_TIMETABLE = "temptt";
	private static final String TABLE_IMPORTANT_DATES = "impdates";
	private static final String TABLE_PAPERS = "paper";

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) 
	{
		super(context, DB_NAME, factory, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase dbb) 
	{
		
		createSchema(dbb);
		
		populateDB(dbb);
		
		/*String decatttrig = "CREATE TRIGGER decatttrig AFTER INSERT ON absent BEGIN "
									+ "UPDATE subjects SET attended=attended-1 WHERE name=new.subject;" +
										"END;";
		
		String incatttrig = "CREATE TRIGGER incatttrig AFTER DELETE ON absent BEGIN "
									+ "UPDATE subjects SET attended=attended+1 WHERE name=old.subject;" +
										"END;";
		
		String inctemptrig = "CREATE TRIGGER inctemptrig AFTER INSERT ON temptt BEGIN "
									+ "UPDATE subjects SET held=held+1 WHERE name=new.subject;" +
										"END;";
		
		String dectemptrig = "CREATE TRIGGER dectemptrig AFTER DELETE ON temptt BEGIN "
									+ "UPDATE subjects SET held=held-1 WHERE name=old.subject;" +
										"END;";  */
		
		/*dbb.execSQL(incatttrig);
		dbb.execSQL(decatttrig);
		dbb.execSQL(inctemptrig);
		dbb.execSQL(dectemptrig); */
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) { }
	
	private void createSchema(SQLiteDatabase db) {
		
		String subjectsSql = "CREATE TABLE "+TABLE_SUBJECTS+"(name TEXT, colour INTEGER, "
				+ "expected INTEGER, held INTEGER, attended INTEGER, safebunks INTEGER, alarm INTEGER);";

		String timetableSql = "CREATE TABLE "+TABLE_TIMETABLE+"(day INTEGER PRIMARY KEY, "
				+ "one TEXT, two TEXT, "
				+ "three TEXT, four TEXT, "
				+ "five TEXT, six TEXT);";
		
		String absentSql = "CREATE TABLE "+TABLE_ABSENT+"(date TEXT, hour INTEGER, subject TEXT);";
		
		String tempttSql = "CREATE TABLE "+TABLE_TEMP_TIMETABLE+"(date TEXT, hour INTEGER, subject TEXT);";
		
		String impdatesSql = "CREATE TABLE "+TABLE_IMPORTANT_DATES+"(name TEXT, date TEXT, desc TEXT, alarm INTEGER);";
		
		String papersSql = "CREATE TABLE "+TABLE_PAPERS+"(name TEXT, subject TEXT, weight INTEGER, " +
				"date TEXT, status INTEGER, maxmarks INTEGER, score INTEGER INTEGER, reminder INTEGER);";
		
		db.execSQL(subjectsSql);
		db.execSQL(timetableSql);
		db.execSQL(absentSql);
		db.execSQL(tempttSql);
		db.execSQL(impdatesSql);
		db.execSQL(papersSql);
	}
	
	private void populateDB(SQLiteDatabase db) {
		
		String monSql = "INSERT INTO timetable VALUES (0, 'free', 'free', 'free', 'free', 'free', 'free');";
		String tueSql = "INSERT INTO timetable VALUES (1, 'free', 'free', 'free', 'free', 'free', 'free');";
		String wedSql = "INSERT INTO timetable VALUES (2, 'free', 'free', 'free', 'free', 'free', 'free');";
		String thuSql = "INSERT INTO timetable VALUES (3, 'free', 'free', 'free', 'free', 'free', 'free');";
		String friSql = "INSERT INTO timetable VALUES (4, 'free', 'free', 'free', 'free', 'free', 'free');";
		String satSql = "INSERT INTO timetable VALUES (5, 'free', 'free', 'free', 'free', 'free', 'free');";
		
		db.execSQL(monSql);
		db.execSQL(tueSql);
		db.execSQL(wedSql);
		db.execSQL(thuSql);
		db.execSQL(friSql);
		db.execSQL(satSql);
	}

	public void exec(String n) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(n);
		db.close();
	}


	// SUBJECTS table handling functions
	public String[] getAllNames() 
	{
		int i = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		long n = DatabaseUtils.queryNumEntries(db, "subjects");
		String[] names=new String[(int) n];
		
		Cursor c = db.rawQuery("SELECT name FROM subjects;", null);
		if (c.moveToFirst()) 
		{
			do
			{
				names[i] = c.getString(0);
				i++;
			} while (c.moveToNext());
		}
		db.close();
		Arrays.sort(names);
		return names;
	}
	
	public void changeColour(String subject, int colour)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "UPDATE subjects SET colour = "+colour+" WHERE name = '"+subject+"';";
		db.execSQL(sql);
		db.close();
	}
	
	public int getColour(String subject)
	{
		String sql = "SELECT colour FROM subjects WHERE name = '"+subject+"';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c=db.rawQuery(sql, null);
		if(c.moveToFirst())
		{
			return c.getInt(0);
		}
		return 0;
	}

	public void addSubject(String subject) throws SQLException// adds a new
																// subject to
																// the DB
	{
		String n = "INSERT INTO subjects VALUES('" + subject + "', null, 0, 0, 0, 0, 0);";
		exec(n);
	}

	public void deleteSubject(String subject) 
	{
		String n1 = "DELETE FROM subjects WHERE name='" + subject + "';";
		String n2 = "DELETE FROM temptt WHERE subject='" + subject + "';";
		String n3 = "DELETE FROM absent WHERE subject='" + subject + "';";
		String n4 = "UPDATE timetable SET one = 'free' WHERE one = '"+subject+"';";
		String n5 = "UPDATE timetable SET two = 'free' WHERE two = '"+subject+"';";
		String n6 = "UPDATE timetable SET three = 'free' WHERE three = '"+subject+"';";
		String n7 = "UPDATE timetable SET four = 'free' WHERE four = '"+subject+"';";
		String n8 = "UPDATE timetable SET five = 'free' WHERE five = '"+subject+"';";
		String n9 = "UPDATE timetable SET six = 'free' WHERE six = '"+subject+"';";
		exec(n1);
		exec(n2);
		exec(n3);
		exec(n4);
		exec(n5);
		exec(n6);
		exec(n7);
		exec(n8);
		exec(n9);
	}
	
	public void setExpected(String subject, int value) 
	{
		String n = "UPDATE subjects SET expected = "+value+" WHERE name = '"+subject+"';";
		exec(n);
	}
	
	public void setHeld(String subject, int value) 
	{
		String n = "UPDATE subjects SET held = "+value+" WHERE name = '"+subject+"';";
		exec(n);
	}
	
	public void setAttended(String subject, int value) 
	{
		String n = "UPDATE subjects SET attended = "+value+" WHERE name = '"+subject+"';";
		exec(n);
	}
	
	public void setSafeBunks(String subject, int value) 
	{
		String n = "UPDATE subjects SET safebunks = "+value+" WHERE name = '"+subject+"';";
		exec(n);
	}

	public void updateExpected(String subject, int value) 
	{
		String n = "UPDATE subjects SET expected = "+value+" WHERE name = '"+subject+"';";
		exec(n);
	}
	
	public void updateHeld(String subject, int value) 
	{
		String n = "UPDATE subjects SET held = held+"+value+" WHERE name = '"+subject+"';";
		exec(n);
	}
	
	public void updateAttended(String subject, int value) 
	{
		String n = "UPDATE subjects SET attended = attended+"+value+" WHERE name = '"+subject+"';";
		exec(n);
	}

	public int getExpected(String subject) 
	{
		String n = "SELECT expected FROM subjects WHERE name = '"
				+ subject+ "';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(n, null);
		int ret = -1;
		if(c.moveToFirst())
			ret = c.getInt(0);
		db.close();
		return ret;
	}
	
	public int[] getSubject(String name)
	{
		SQLiteDatabase db=this.getReadableDatabase();
		String n="SELECT colour, expected, held, attended FROM subjects WHERE name='"+name+"';";
		Cursor c=db.rawQuery(n, null);
		if(c.moveToFirst())
		{
			return new int[] {c.getInt(1), c.getInt(2), c.getInt(3) };
		}
		return null;
	}
	
	public int getHeld(String subject) 
	{
		String n = "SELECT held FROM subjects WHERE name = '"
				+ subject + "';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(n, null);
		c.moveToFirst();
		int ret = c.getInt(0);
		db.close();
		return ret;
	}

	public int getAttended(String subject) 
	{
		String n = "SELECT attended FROM subjects WHERE name = '"
				+ subject + "';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(n, null);
		c.moveToFirst();
		int ret = c.getInt(0);
		db.close();
		return ret;
	}
	
	public int getSafeBunks(String subject) 
	{
		String n = "SELECT safebunks FROM subjects WHERE name = '"
				+ subject + "';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(n, null);
		c.moveToFirst();
		int ret = c.getInt(0);
		db.close();
		return ret;
	}
	
	public void incrementHeld(String sub)
	{
		String sql = "UPDATE subjects SET held=held+1 WHERE name='"+sub+"';";
		exec(sql);
	}
	
	public boolean isNotifOn(String subject)
	{
		String sql = "SELECT alarm FROM subjects WHERE name = '"+subject+"';";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int val = c.getInt(0);
		db.close();
		
		if(val == 1) return true;
		
		return false;
	}

	// TIMETABLE table handling functions

	public void changeHour(int day, int hour, String subject) 
	{
		String hr;
		switch (hour) 
		{
			case 0:	hr = "one";	break;
			case 1:	hr = "two";	break;
			case 2:	hr = "three"; break;
			case 3:	hr = "four"; break;
			case 4:	hr = "five"; break;
			case 5:	hr = "six"; break;
			default: return;
		}
		String n = "UPDATE timetable SET " + hr + "= '" + subject + "' WHERE day= " + day + ";";
		exec(n);
	}

	public Timetable getTimetable() 
	{
		String n = "SELECT *FROM timetable;";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(n, null);
		
		Timetable tt = new Timetable();
		c.moveToFirst();
		for(int i=0; i<6; i++)
		{
			for(int j=0; j<6; j++)
				tt.setHour(i, j, c.getString(j+1));
			
			c.moveToNext();
		}
		db.close();
		return tt;
	}
	
	// SEMDATES table handling functions	
	public void changeStartOrEnd(boolean startorend, Calendar cal)
	{
		String typ;
		if(startorend) typ="'start';";
		else typ="'end';";
		
		String update="UPDATE semdates SET year="+cal.get(Calendar.YEAR)+", month="+cal.get(Calendar.MONTH)+", day="+cal.get(Calendar.DATE)+
				" WHERE type="+typ;
		
		exec(update);
	}
	
	public Calendar getStartOrEnd(boolean startorend)
	{
		String get;
		if(startorend) get="SELECT day, month, year FROM semdates WHERE type='start';";
		else get="SELECT day, month, year FROM semdates WHERE type='end';";
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor c=db.rawQuery(get, null);
		if(c.moveToFirst())
		{
			return new GregorianCalendar(c.getInt(2), c.getInt(1)-1, c.getInt(0));
		}
		return null;
	}
	
	// ABSENT table handling functions
	
	public BunkOrTempTT[] getAllBunks()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		long n = DatabaseUtils.queryNumEntries(db, "absent"); //get count of rows
		BunkOrTempTT bunks[] = new BunkOrTempTT[(int) n];
		
		String query = "SELECT *FROM absent;";
		Cursor c = db.rawQuery(query, null);
		int i = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		if(c.moveToFirst())
		{
			do
			{
				Calendar temp = Calendar.getInstance();
				try 
				{
					temp.setTime(sdf.parse(c.getString(0)));
				} catch (ParseException e) {}
				
				bunks[i++] = new BunkOrTempTT(c.getString(2), temp, c.getInt(1));
			} while(c.moveToNext());
		}
		
		return bunks;
	}
	
	public void addBunk(BunkOrTempTT bunk)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy"); 
		String d = sdf.format((bunk.getDate()).getTime());
		String sql = "INSERT INTO absent VALUES('"+d+ "', "+bunk.getHour()+", '"+bunk.getSubject()+"');";
		exec(sql);
	}
	
	public void removeBunk(BunkOrTempTT bunk)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String d = sdf.format((bunk.getDate()).getTime());
		String sql = "DELETE FROM absent WHERE date='"+d+ "' AND hour="+bunk.getHour()+" AND subject='"
					+bunk.getSubject()+"';";
		exec(sql);
	}
	
	//temptt table handling functions
	
	public BunkOrTempTT[] getAllTempTT()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		long n = DatabaseUtils.queryNumEntries(db, "temptt"); //get count of rows
		BunkOrTempTT temptt[] = new BunkOrTempTT[(int) n];
		
		String query = "SELECT *FROM temptt;";
		Cursor c = db.rawQuery(query, null);
		int i = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		if(c.moveToFirst())
		{
			do
			{
				Calendar temp = Calendar.getInstance();
				try 
				{
					temp.setTime(sdf.parse(c.getString(0)));
				} catch (ParseException e) {}
		
				temptt[i++] = new BunkOrTempTT(c.getString(2), temp, c.getInt(1));
			} while(c.moveToNext());
		}
		
		return temptt;
	}
	
	public void addTempTT(BunkOrTempTT temptt)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy"); 
		String d = sdf.format((temptt.getDate()).getTime());
		String sql = "INSERT INTO temptt VALUES('"+d+ "', "+temptt.getHour()+", '"+temptt.getSubject()+"');";
		exec(sql);
	}
	
	public void removeTempTT(BunkOrTempTT bunk)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy"); 
		String d = sdf.format((bunk.getDate()).getTime());
		String sql = "DELETE FROM temptt WHERE date='"+d+ "' AND hour="+bunk.getHour()+" AND subject='"
					+bunk.getSubject()+"';";
		exec(sql);
	}
	
	public String getTempSubject(Calendar cal, int hour)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String date = sdf.format(cal.getTime());
		
		String sql = "SELECT subject FROM temptt WHERE date='"+date+"' AND hour="+hour+";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(sql, null);
		
		if(c.moveToFirst())
			return c.getString(0);
		
		return "";
	}
	
	public boolean hourOfDateExistsInTempTT(Calendar cal, int hour)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String date = sdf.format(cal.getTime());
		
		String sql = "SELECT subject FROM temptt WHERE date='"+date+"' AND hour="+hour+";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(sql, null);
		
		return c.moveToFirst();
	}
	
	public void resetDB()
	{
		String sql1 = "DELETE FROM subjects;";
		String sql2 = "DELETE FROM absent;";
		String sql3 = "DELETE FROM temptt;";
		String sql4 = "DELETE FROM impdates;";
		String sql5 = "UPDATE timetable SET one='free', two='free', three='free', four='free', five='free', six='free';";
		exec(sql1);
		exec(sql2);
		exec(sql3);
		exec(sql4);
		exec(sql5);
	}
	
	public void resetSubjectAttribs()
	{
		String sql1 = "UPDATE subjects SET expected=0;";
		String sql2 = "UPDATE subjects SET attended=attended-held;"; //to preserve the bunk calculated by trigger
		String sql3 = "UPDATE subjects SET held=0;";
		exec(sql1);
		exec(sql2);
		exec(sql3);
	}
	
	//IMPDATES handler functions
	
	public int getImpDatesCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		long n = DatabaseUtils.queryNumEntries(db, "impdates");
		return (int) n;
	}
	
	public String[] getAllImpDateNames()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT name FROM impdates;", null);
		
		String dates [] = new String[getImpDatesCount()];
		
		int i = 0;
		if(c.moveToFirst())
		{
			do
			{
				dates[i++] = c.getString(0);
			}
			while(c.moveToNext());
		}
		return dates;
	}
	
	public String[][] getAllImpDates()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT *FROM impdates;", null);
		
		String dates [][] = new String[getImpDatesCount()][3];
		
		int i = 0;
		if(c.moveToFirst())
		{
			do
			{
				dates[i][0] = c.getString(0);
				dates[i][1] = c.getString(1);
				dates[i][2] = c.getString(2);
				i++;
			}
			while(c.moveToNext());
		}
		return dates;
	}
	
	public String[] getImpDateDetails(String name)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT *FROM impdates WHERE name = '"+name+"';", null);
		
		String dets[] = new String[4];
		
		if(c.moveToFirst())
		{
			dets[0] = c.getString(0);
			dets[1] = c.getString(1);
			dets[2] = c.getString(2);
			dets[3] = String.valueOf(c.getInt(3));
		}	
		
		return dets;
	}
	
	public void addImpDate(String name, String date, String desc, boolean alarm)
	{
		int val = 0;
		if(alarm) val = 1;
		String sql = "INSERT INTO impdates VALUES ('"+name+"', '"+date+"', '"+desc+"', "+val+");";
		exec(sql);
	}
	
	public void deleteImpDate(String name)
	{
		String sql = "DELETE FROM impdates WHERE name='"+name+"';";
		exec(sql);
	}
	
	public boolean impDatesAlarm(String name)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT alarm FROM impdates WHERE name = "+name+";", null);
		int val = 0;
		if(c.moveToFirst())
			val = c.getInt(0);
		
		if(val == 1) return true;
		return false;
	}
	
	public boolean lowNotifAlarm(String name)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT alarm FROM subjects WHERE name = "+name+";", null);
		int val = 0;
		if(c.moveToFirst())
			val = c.getInt(0);
		
		if(val == 1) return true;
		return false;
	}
	
	public void toggleAlarm(String subject)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String al = "0";
		if(lowNotifAlarm(subject)) al = "1";
		String sql = "UPDATE subjects SET alarm='"+al+"' WHERE name='"+subject+"';";
		exec(sql);
	}
}