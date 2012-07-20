package myapp.thukydientu.model;

import java.util.ArrayList;
import java.util.List;

public class TimeDuration {
	private long StartTime;
	private long EndTime;
	
	public TimeDuration(long startTime, long endTime) {
		this.StartTime = startTime;
		this.EndTime = endTime;
	}
	
	public long getStartTime() {
		return StartTime;
	}
	
	public void setStartTime(long startTime) {
		this.StartTime = startTime;
	}
	
	public long getEndTime() {
		return EndTime;
	}
	
	public void setEndTime(long endTime) {
		this.EndTime = endTime;
	}
	
	public long getDuration() {
		return this.EndTime - this.StartTime;
	}
	
	public boolean isContained(TimeDuration timeDurationObject) {
		if (timeDurationObject.getStartTime() > StartTime && timeDurationObject.getStartTime() < EndTime)
			return true;
		
		if (timeDurationObject.getEndTime() > StartTime && timeDurationObject.getEndTime() < EndTime)
			return true;
		
		return false;
	}

	public List<TimeDuration> subtract(TimeDuration subtractObject) {
		List<TimeDuration> result = new ArrayList<TimeDuration>();
		
		if (subtractObject.getDuration() < this.getDuration()) {
			
			if (subtractObject.getStartTime() <= this.StartTime) 
				result.add(new TimeDuration(subtractObject.getEndTime(), this.EndTime));
			else {
				result.add(new TimeDuration(this.StartTime, subtractObject.getStartTime()));
				if (subtractObject.getEndTime() < this.EndTime) 
					result.add(new TimeDuration(subtractObject.EndTime, this.EndTime));
			}
			
		}
		
		return result;
	}
}
