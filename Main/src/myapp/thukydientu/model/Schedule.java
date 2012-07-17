package myapp.thukydientu.model;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Schedule {
	private long Id;
	private String DateSet;
	private String modified;
	private int DayName;
	private String Time;
	private String Subject;
	private String ClassName;
	private String SchoolName;
	private int changed;
	private int deleted;
	private int lessons;
	private int lessonDuration;
	
	public Schedule() {
		modified = "";
		DateSet = "";
	}
	
	public Schedule(long Id, int dayName, String time, String dateSet, String subject, String className, String schoolName) {
		this.Id = Id;
		this.DayName = dayName;
		this.Time = time;
		this.DateSet = dateSet;
		this.Subject = subject;
		this.ClassName = className;
		this.SchoolName = schoolName;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}
	
	public int getDayName() {
		return DayName;
	}

	public void setDayName(int dayName) {
		DayName = dayName;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getSubject() {
		return Subject;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}

	public String getDateSet() {
		return DateSet;
	}
	public void setDateSet(String dateSet) {
		this.DateSet = dateSet;
	}
	public void setDateSet(long milisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		this.DateSet = sdf.format(new Date(milisecond));
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

	public int getChanged() {
		return changed;
	}

	public void setChanged(int changed) {
		this.changed = changed;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public int getLessons() {
		return lessons;
	}

	public void setLessons(int lessons) {
		this.lessons = lessons;
	}

	public int getLessonDuration() {
		return lessonDuration;
	}

	public void setLessonDuration(int lessonDuration) {
		this.lessonDuration = lessonDuration;
	}

}
