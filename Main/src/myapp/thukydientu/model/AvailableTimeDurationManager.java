package myapp.thukydientu.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AvailableTimeDurationManager {
	private long StartPoint;
	private long EndPoint;
	private long NeededDuration;
	private List<TimeDuration> AvailableDurationList;
	
	public AvailableTimeDurationManager(long startPoint, long endPoint) {
		AvailableDurationList = new ArrayList<TimeDuration>();
		StartPoint = startPoint;
		EndPoint = endPoint;
		TimeDuration first = new TimeDuration();
		first.setStart(startPoint);
		first.setEnd(endPoint);
		AvailableDurationList.add(first);
	}
	
	public long getStartPoint() {
		return StartPoint;
	}
	public void setStartPoint(long startPoint) {
		StartPoint = startPoint;
	}
	public long getEndPoint() {
		return EndPoint;
	}
	public void setEndPoint(long endPoint) {
		EndPoint = endPoint;
	}
	public long getDurationNeed() {
		return NeededDuration;
	}
	public void setDurationNeed(long durationNeed) {
		NeededDuration = durationNeed;
	}
	public List<TimeDuration> getAvailableDurationList() {
		return AvailableDurationList;
	}
	public void setAvailableDurationList(List<TimeDuration> availableDurationList) {
		AvailableDurationList = availableDurationList;
	}
	public List<TimeDuration> removeDeadTimeDuration(TimeDuration subtracted, TimeDuration subtractObject) {
		List<TimeDuration> result = new ArrayList<TimeDuration>();
		TimeDuration temp;
		if (subtractObject.getStart() < subtracted.getStart() + NeededDuration) {
			if (subtractObject.getEnd() + NeededDuration < subtracted.getEnd()) {
				temp = new TimeDuration();
				temp.setStart(subtractObject.getEnd());
				temp.setEnd(subtracted.getEnd());
				result.add(temp);
			}
		} else {
			temp = new TimeDuration();
			temp.setStart(subtracted.getStart());
			temp.setEnd(subtractObject.getStart());
			result.add(temp);
			if (subtractObject.getEnd() + NeededDuration < subtracted.getEnd()) {
				temp = new TimeDuration();
				temp.setStart(subtractObject.getEnd());
				temp.setEnd(subtracted.getEnd());
				result.add(temp);
			}
		}
		return result;
	}
	public void addDeadTimeDuration(TimeDuration deadDuration) {
		for (TimeDuration availableDuration : AvailableDurationList) {
			if (availableDuration.isContained(deadDuration)) {
				List<TimeDuration> temp = new ArrayList<TimeDuration>();
				temp = removeDeadTimeDuration(availableDuration, deadDuration);
				AvailableDurationList.remove(availableDuration);
				if (temp.size() > 0) {
					AvailableDurationList.addAll(temp);
				}
			}
		}
		Collections.sort(AvailableDurationList, timeDurationComparator);
	}
	private Comparator<TimeDuration> timeDurationComparator = new Comparator<TimeDuration>() {
		
		@Override
		public int compare(TimeDuration object1, TimeDuration object2) {
			if (object1.getStart() > object2.getStart())
				return 1;
			if (object1.getStart() < object2.getStart())
				return -1;
			return 0;
		}
	};
}
