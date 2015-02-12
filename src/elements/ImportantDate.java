package elements;

import java.util.GregorianCalendar;

/**
 * @author Vedavyas Bhat
 * 
 * A POJO which represents a database element
 */
public class ImportantDate {
	
	String name, description;
	GregorianCalendar date;
	boolean reminder;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public GregorianCalendar getDate() {
		return date;
	}
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	public boolean isReminder() {
		return reminder;
	}
	public void setReminder(boolean reminder) {
		this.reminder = reminder;
	}
	
}
