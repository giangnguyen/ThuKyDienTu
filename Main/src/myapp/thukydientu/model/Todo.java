package myapp.thukydientu.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Todo {
	private long Id;
	private int UserId;
	private String DateStart;
	private String DateEnd;
	private String TimeFrom;
	private String TimeUntil;
	private String Title;
	private String Work;
	private int Alarm;
	private String DateSet;
	private String Modified;
	private int Changed;
	private int Deleted;
	
	public Todo() {
		Id = -1;
	}
	
	public long getId() {
		return Id;
	}
	
	public void setId(long Id) {
		this.Id = Id;
	}
	public String getDateStart() {
		return DateStart;
	}
	public void setDateStart(String DateStart) {
		this.DateStart = DateStart;
	}
	public String getDateEnd() {
		return DateEnd;
	}
	public void setDateEnd(String DateEnd) {
		this.DateEnd = DateEnd;
	}
	public String getTimeFrom() {
		return TimeFrom;
	}
	public void setTimeFrom(String TimeFrom) {
		this.TimeFrom = TimeFrom;
	}
	public String getTimeUntil() {
		return TimeUntil;
	}
	public void setTimeUntil(String TimeUntil) {
		this.TimeUntil = TimeUntil;
	}
	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}
	public String getWork() {
		return Work;
	}
	public void setWork(String Work) {
		this.Work = Work;
	}
	public int getAlarm() {
		return Alarm;
	}
	public void setAlarm(int Alarm) {
		this.Alarm = Alarm;
	}
	public String getDateSet() {
		return DateSet;
	}
	public void setDateSet(String DateSet) {
		this.DateSet = DateSet;
	}
	public void setDateSet(long milisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		this.DateSet = sdf.format(new Date(milisecond));
	}

	public String getModified() {
		return Modified;
	}

	public void setModified(String Modified) {
		this.Modified = Modified;
	}
	
	public void setModified(long milisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		this.Modified = sdf.format(new Date(milisecond));
	}

	public int getDeleted() {
		return Deleted;
	}

	public void setDeleted(int Deleted) {
		this.Deleted = Deleted;
	}

	public int getChanged() {
		return Changed;
	}

	public void setChanged(int Changed) {
		this.Changed = Changed;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}
}
