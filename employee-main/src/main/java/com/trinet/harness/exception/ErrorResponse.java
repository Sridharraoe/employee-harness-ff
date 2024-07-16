package com.trinet.harness.exception;

import java.time.LocalDate;

public class ErrorResponse {
	String message;
	String details;
	LocalDate timeStamp;

	public ErrorResponse(LocalDate timeStamp, String message, String details) {
		this.timeStamp = timeStamp;
		this.message = message;
		this.details = details;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public LocalDate getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDate timeStamp) {
		this.timeStamp = timeStamp;
	}

}
