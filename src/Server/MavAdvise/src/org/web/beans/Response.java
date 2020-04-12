package org.web.beans;

public class Response {
	public String type;
	public String message;
	public Object result;

	public Response() {
		type = "failed";
		message = "";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.type = "success";
		this.result = result;
	}
}
