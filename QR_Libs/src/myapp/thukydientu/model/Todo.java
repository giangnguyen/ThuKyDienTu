package myapp.thukydientu.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Todo {
	private long Id;
	private String dateStart;
	private String dateEnd;
	private String timeFrom;
	private String timeUntil;
	private String title;
	private String work;
	private int alarm;
	private String dateSet;
	private String modified;
	private int changed;
	private int deleted;
	
	public long getId() {
		return Id;
	}
	
	public void setId(long Id) {
		this.Id = Id;
	}
	public String getDateStart() {
		return dateStart;
	}
	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}
	public String getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}
	public String getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}
	public String getTimeUntil() {
		return timeUntil;
	}
	public void setTimeUntil(String timeUntil) {
		this.timeUntil = timeUntil;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public int getAlarm() {
		return alarm;
	}
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}
	public String getDateSet() {
		return dateSet;
	}
	public void setDateSet(String dateSet) {
		this.dateSet = dateSet;
	}
	public void setDateSet(long milisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		this.dateSet = sdf.format(new Date(milisecond));
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public void setModified(long milisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		this.modified = sdf.format(new Date(milisecond));
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public int getChanged() {
		return changed;
	}

	public void setChanged(int changed) {
		this.changed = changed;
	}
}
