package myapp.thukydientu.controller;

import java.util.ArrayList;
import java.util.List;

import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.model.TimeDuration;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TimeUtils;
import myapp.thukydientu.util.TodoUtils;
import android.app.Activity;

public class HintTimeManager {
	private long startTime;
	private long endTime;
	private int startDate;
	private int endDate;
	private long duration;
	
	public HintTimeManager(long startTime, long endTime, int startDate, int endDate, long duration) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
	}
	
	public int[] getListDays() {
		final int count = endDate - startDate + 1;
		int[] days = new int[count];
		
		for (int i = 0; i < count; i ++) {
			days[i] = startDate + i;
		}
		
		return days;
	}
	
	public List<TimeDuration> getHintTimeByDay(Activity activity, int dayOfMonth) {
		List<TimeDuration> hints = new ArrayList<TimeDuration>();
		hints.add(new TimeDuration(this.startTime, this.endTime));
		
		List<Schedule> listSchedule = ScheduleUtils.getListScheduleByDay(activity, dayOfMonth);
		for (Schedule schedule : listSchedule) {
			final long startTime = TimeUtils.getTimeInMilisecond(schedule.getTime());
			final long endTime = startTime + schedule.getLessons() * schedule.getLessonDuration() * 60 * 1000;
			removeDeadTime(hints, new TimeDuration(startTime, endTime));
		}
		
		List<Todo> listTodo = TodoUtils.getListTodoByDay(activity, dayOfMonth);
		for (Todo todo : listTodo) {
			final long startTime = TimeUtils.getTimeInMilisecond(todo.getTimeFrom());
			final long endTime = TimeUtils.getTimeInMilisecond(todo.getTimeUntil());
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
