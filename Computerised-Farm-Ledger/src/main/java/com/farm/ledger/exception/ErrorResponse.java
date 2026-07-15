package com.farm.ledger.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class ErrorResponse {
	
	private String errorCode;
	private String errorMessage;
	
	@JsonInclude(value = Include.NON_EMPTY)
	private List<Validation> validation;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<Validation> getValidation() {
		return validation;
	}

	public void setValidation(List<Validation> validation) {
		this.validation = validation;
	}

}
