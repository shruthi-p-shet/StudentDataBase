package com.jsp.StudentManagementSystem.utility;

public class ErrorStructure {
private int status;
private String rootCause;
private String message;

public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}
public String getRootCause() {
	return rootCause;
}
public void setRootCause(String rootCause) {
	this.rootCause = rootCause;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
}
