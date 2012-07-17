package myapp.thukydientu.model;

public class TimeDuration {
	private long Start;
	private long End;
	public long getStart() {
		return Start;
	}
	public void setStart(long start) {
		Start = start;
	}
	public long getEnd() {
		return End;
	}
	public void setEnd(long end) {
		End = end;
	}
	public boolean isContained(TimeDuration timeDurationObject) {
		if (timeDurationObject.getStart() > Start && timeDurationObject.getStart() < End)
			return true;
		
		if (timeDurationObject.getEnd() > Start && timeDurationObject.getEnd() < End)
			return true;
		
		return false;
	}
}
