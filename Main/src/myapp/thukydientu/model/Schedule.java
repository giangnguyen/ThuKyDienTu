package myapp.thukydientu.model;

public class Schedule {
	private long Id;
	private int UserId;
	private String DateSet;
	private String Modified;
	private int DayName;
	private String Time;
	private String Subject;
	private String ClassName;
	private String SchoolName;
	private int Changed;
	private int Deleted;
	private int Lessons;
	private int LessonDuration;

	public Schedule() {
		Modified = "";
		DateSet = "";
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

	public String getModified() {
		return Modified;
	}

	public void setModified(String Modified) {
		this.Modified = Modified;
	}

	public int getChanged() {
		return Changed;
	}

	public void setChanged(int Changed) {
		this.Changed = Changed;
	}

	public int getDeleted() {
		return Deleted;
	}

	public void setDeleted(int Deleted) {
		this.Deleted = Deleted;
	}

	public int getLessons() {
		return Lessons;
	}

	public void setLessons(int Lessons) {
		this.Lessons = Lessons;
	}

	public int getLessonDuration() {
		return LessonDuration;
	}

	public void setLessonDuration(int LessonDuration) {
		this.LessonDuration = LessonDuration;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

}
