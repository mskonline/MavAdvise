package org.web.beans;

public class User {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String roleType;
	private String netID;
	private String utaID;
	private String branch;
	private int securityQuestionID;
	private String securityAnswer;

	private String deviceID;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getNetID() {
		return netID;
	}
	public void setNetID(String netID) {
		this.netID = netID;
	}
	public String getUtaID() {
		return utaID;
	}
	public void setUtaID(String utaID) {
		this.utaID = utaID;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public int getSecurityQuestionID() {
		return securityQuestionID;
	}
	public void setSecurityQuestionID(int securityQuestionID) {
		this.securityQuestionID = securityQuestionID;
	}
	public String getSecurityAnswer() {
		return securityAnswer;
	}
	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public boolean validate(){
		//TODO
		return true;
	}

	public boolean authenticate(String password){
		return password.equalsIgnoreCase(this.password) ? true : false;
	}
}
