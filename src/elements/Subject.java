package elements;

public class Subject {
	String name;
	int expected, held, attended, safeBunks;
	boolean hasLowAttendanceAlarm;

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

	public boolean isHasLowAttendanceAlarm() {
		return hasLowAttendanceAlarm;
	}

	public void setHasLowAttendanceAlarm(boolean hasLowAttendanceAlarm) {
		this.hasLowAttendanceAlarm = hasLowAttendanceAlarm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
