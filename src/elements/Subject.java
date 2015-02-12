package elements;

/**
 * @author Vedavyas Bhat
 * 
 * A POJO which represents a database element
 */
public class Subject implements Comparable<Subject> {
	String name;
	int expected, held, attended, safeBunks;
	boolean lowAttendanceAlarm;

	public int getExpected() {
		return expected;
	}

	public void setExpected(int expected) {
		this.expected = expected;
		
	}

	public int getHeld() {
		return held;
	}

	public void setHeld(int held) {
		this.held = held;
	}

	public int getAttended() {
		return attended;
	}

	public void setAttended(int attended) {
		this.attended = attended;
	}

	public int getSafeBunks() {
		return safeBunks;
	}

	public void setSafeBunks(int safeBunks) {
		this.safeBunks = safeBunks;
	}

	public boolean hasLowAttendanceAlarm() {
		return lowAttendanceAlarm;
	}

	public void setLowAttendanceAlarm(boolean lowAttendanceAlarm) {
		this.lowAttendanceAlarm = lowAttendanceAlarm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Subject another) {
		return this.getName().compareTo(another.getName());
	}
}
