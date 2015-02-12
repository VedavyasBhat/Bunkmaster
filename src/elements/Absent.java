package elements;

import java.util.GregorianCalendar;

/**
 * @author Vedavyas Bhat
 * 
 * A POJO which represents a database element
 */
public class Absent {
	
	GregorianCalendar date;
	int hour;
	Subject subject;
	
	public GregorianCalendar getDate() {
		return date;
	}
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
}
