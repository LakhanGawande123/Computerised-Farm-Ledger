package com.farm.ledger.exception;

import lombok.Data;

@Data
public class Validation {
	
	private String field;
	private String errorMessage;

}
