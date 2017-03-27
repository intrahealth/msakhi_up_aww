package org.intrahealth.mnewborncare.awc;

public class PregInfo {
	int id;
	String name;
	String edd;
	String dob;
	String moblile_No;
	String month;
	String edd_mnths;
	public String getEdd_mnths() {
		return edd_mnths;
	}
	public void setEdd_mnths(String edd_mnths) {
		this.edd_mnths = edd_mnths;
	}
	int server_id;
    String gender;
	int pos;
	public int getServer_id() {
		return server_id;
	}
	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}
	int food;
	int weight_food;
	float weight_size;
	
	public PregInfo() {
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEdd() {
		return edd;
	}
	public int getFood() {
		return food;
	}
	public void setFood(int food) {
		this.food = food;
	}
	public int getWeight_food() {
		return weight_food;
	}
	public void setWeight_food(int weight_food) {
		this.weight_food = weight_food;
	}
	public float getWeight_size() {
		return weight_size;
	}
	public void setWeight_size(float i) {
		this.weight_size = i;
	}
	public void setEdd(String edd) {
		this.edd = edd;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getMoblile_No() {
		return moblile_No;
	}
	public void setMoblile_No(String moblile_No) {
		this.moblile_No = moblile_No;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String string) {
		this.month = string;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public void setgender(String gender) {
		// TODO Auto-generated method stub
		this.gender=gender;
	}
	public String getgender() {
		// TODO Auto-generated method stub
		return gender;
	}

}
