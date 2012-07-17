package myapp.thukydientu.model;

public class User {
	private int userId;
	private String userName;
	private String password;
	private String fullname;
	private String email;
	private int phone;
	private String address;
	private String dateOfBith;
	private boolean isMan;
	private String workplace;
	private String job;
	
	public User () {
		this.userId = 0;
		this.userName = "";
		this.password = "";
		this.fullname = "";
		this.email = "";
		this.phone = 0;
		this.address = "";
		this.dateOfBith = "";
		this.isMan = false;
		this.workplace = "";
		this.job = "";
		
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDateOfBith() {
		return dateOfBith;
	}

	public void setDateOfBith(String dateOfBith) {
		this.dateOfBith = dateOfBith;
	}

	public boolean isMan() {
		return isMan;
	}

	public void setMan(boolean isMan) {
		this.isMan = isMan;
	}

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
}
