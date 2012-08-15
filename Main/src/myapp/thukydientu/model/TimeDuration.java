package myapp.thukydientu.model;

import java.util.ArrayList;
import java.util.List;

public class TimeDuration {
	private long StartMillis;
	private long EndMillis;

	public TimeDuration(long startMillis, long endMillis) {
		this.StartMillis 	= startMillis;
		this.EndMillis   	= endMillis;
	}
	
	public long getStartMillis() {
		return StartMillis;
	}

	public void setStartMillis(long startMillis) {
		StartMillis = startMillis;
	}

	public long getEndMillis() {
		return EndMillis;
	}

	public void setEndMillis(long endMillis) {
		EndMillis = endMillis;
	}

	public long getDuration() {
		return EndMillis - StartMillis;
	}
	
	public boolean isContained(TimeDuration timeDurationObject) {
		if ((timeDurationObject.getStartMillis() > StartMillis && 
			 timeDurationObject.getStartMillis() < EndMillis)
			|| 
			(timeDurationObject.getEndMillis() > StartMillis) &&
			 timeDurationObject.getEndMillis() < EndMillis)
		{
			return true;
		}
		
		return false;
	}

	public List<TimeDuration> subtract(TimeDuration subtractObject) {
		List<TimeDuration> results = new ArrayList<TimeDuration>();
		
		if (subtractObject.getStartMillis() >= StartMillis + getDuration()) {
			results.add(new TimeDuration(StartMillis, subtractObject.getStartMillis()));
		}
		
		if (subtractObject.getEndMillis() <= EndMillis - getDuration()) {
			results.add(new TimeDuration(subtractObject.getEndMillis(), EndMillis));
		}

		return results;
	}
}
