package com.farm.ledger.exception;

import java.util.Arrays;

public class FarmLedgerServiceException extends RuntimeException {
	
	private static final long serialVersionID = 1L;
	
	private final String code;
	private final String[] arrayOfValues;
	private final String message;
	
	public FarmLedgerServiceException() {
		this.code = null;
		this.arrayOfValues = null;
		this.message = null;
	}
	
	public FarmLedgerServiceException(String code) {
		super(code);
		this.code = code;
		this.arrayOfValues = null;
		this.message = null;
	}

	public FarmLedgerServiceException(String code, String... arrayOfValues) {
		super(code);
		this.code = code;
		this.arrayOfValues = Arrays.copyOf(arrayOfValues, arrayOfValues.length);
		this.message = "";
	}
	
	public FarmLedgerServiceException(String code, String[] arrayOfValues, Throwable exception) {
		super(code, exception);
		this.code = code;
		this.arrayOfValues = Arrays.copyOf(arrayOfValues, arrayOfValues.length);
		this.message = null;
	}
	
	public FarmLedgerServiceException(String code, String[] arrayOfValues, Throwable exception, String message) {
		super(code, exception);
		this.code = code;
		this.arrayOfValues = Arrays.copyOf(arrayOfValues, arrayOfValues.length);
		this.message = message;
	}
	
	public FarmLedgerServiceException(String code, String[] arrayOfValues, String message) {
		super(code);
		this.code = code;
		this.message = message;
		this.arrayOfValues = Arrays.copyOf(arrayOfValues, arrayOfValues.length);
	}

	public FarmLedgerServiceException(String code, Throwable exception) {
		super(code, exception);
		this.code = code;
		this.arrayOfValues = null;
		this.message = null;
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public String[] getArrayOfValues() {
		if (null == arrayOfValues) {
			return new String[0];
		} else {
			return Arrays.copyOf(arrayOfValues, arrayOfValues.length);
		}
	}

}
