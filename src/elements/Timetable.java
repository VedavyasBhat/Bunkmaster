package elements;

import java.util.ArrayList;
import java.util.Iterator;

public class Timetable implements Iterable<Timetable.Day> {
	
	class Day {
		Weekdays dayOfWeek;
		ArrayList<Subject> hours;
		
		public Day(Weekdays dayOfWeek) {
			this.dayOfWeek = dayOfWeek;
		}

		public ArrayList<Subject> getHours() {
			return hours;
		}

		public void addHour(int hour, Subject subject) {
			hours.set(hour, subject);
		}
		
		public Subject getSubject(int hour) {
			return hours.get(hour);
		}
	}
	
	ArrayList<Day> days;
	
	public ArrayList<Day> getTimetable() {
		return days;
	}

	@Override
	public Iterator<elements.Timetable.Day> iterator() {
		return days.iterator();
	}
}
