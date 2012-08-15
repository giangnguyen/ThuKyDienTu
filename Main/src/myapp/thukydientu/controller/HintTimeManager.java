package myapp.thukydientu.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.model.TimeDuration;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TaleTimeUtils;
import myapp.thukydientu.util.TodoUtils;
import android.app.Activity;

public class HintTimeManager {
	private Calendar Start;
	private Calendar End;
	private long Duration;
	private List<TimeDuration> listHint;
	
	public HintTimeManager(Calendar startTime, Calendar endTime, long duration) {
		Start = startTime;
		
		End = endTime;
		
		this.Duration = duration;
		
		listHint = new ArrayList<TimeDuration>();
		listHint.add(new TimeDuration(Start.getTimeInMillis(), End.getTimeInMillis()));
	}
	
	public List<TimeDuration> getHintTimeByDay(Activity activity,  int dayOfMonth) {
		
		List<Schedule> listSchedule = ScheduleUtils.getListScheduleByDay(activity, dayOfMonth);
		for (Schedule schedule : listSchedule) {
			Calendar calendarStart = TaleTimeUtils.createCalendarByTimeString(schedule.getTime());
			calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			final long endTime = startTime + schedule.getLessons() * schedule.getLessonDuration() * 60 * 1000;
			removeDeadTime(hints, new TimeDuration(startTime, endTime));
		}
		
		List<Todo> listTodo = TodoUtils.getListTodoByDay(activity, dayOfMonth);
		for (Todo todo : listTodo) {
			Calendar startTime = TaleTimeUtils.createCalendarByTimeString(timeString)
			final long startTime = TaleTimeUtils.convert2Milisecond(todo.getTimeFrom());
			final long endTime = TaleTimeUtils.convert2Milisecond(todo.getTimeUntil());
			removeDeadTime(hints, new TimeDuration(startTime, endTime));
		}
		
		for (TimeDuration timeDuration : hints) {
			if (timeDuration.getDuration() < this.duration)
				hints.remove(timeDuration);
		}
		return hints;
	}
	
	private void removeDeadTime(List<TimeDuration> list, TimeDuration object) {
		List<TimeDuration> temp = new ArrayList<TimeDuration>();
		for (TimeDuration timeDuration : list) {
			timeDuration.isContained(object);
			temp.addAll(timeDuration.subtract(object));
			list.remove(timeDuration);
		}
		list.addAll(temp);
	}
	
}
