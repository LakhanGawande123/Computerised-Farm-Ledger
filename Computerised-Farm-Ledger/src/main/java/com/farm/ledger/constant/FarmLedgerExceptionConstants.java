package com.farm.ledger.constant;

public final class FarmLedgerExceptionConstants {
	
	private FarmLedgerExceptionConstants() {
		
	}
	
	public static final String EXCEPTION_CONSTRAINT_VIOLATION = "FARM_LEDGER_ERR_2001";
	
	
	public static final String EXCEPTION_QUERY_PARAM_EMPTY = "FARM_LEDGER_ERR_2002";
	
	public static final String EXCEPTION_SERVER_ERROR = "FARM_LEDGER_ERR_2003";
	
	public static final String EXCEPTION_METHOD_NOT_FOUND = "FARM_LEDGER_ERR_2004";
	
	public static final String EXCEPTION_UNEXPECTED = "FARM_LEDGER_ERR_2005"; 
	
	public static final String EXCEPTION_UNSUPPORTED_MEDIA_TYPE = "FARM_LEDGER_ERR_2006";
	
	public static final String PLOT_NAME_NOT_NULL_OR_EMPTY = "FARM_LEDGER_ERR_2007";

	public static final String FARMER_ID_NOT_FOUND = "FARM_LEDGER_ERR_2008";
	public static final String PLOT_NAME_NOT_BLANK = "FARM_LEDGER_ERR_2009";

	// API version header missing
	public static final String EXCEPTION_API_VERSION_REQUIRED = "FARM_LEDGER_ERR_2010";

	// API version not supported
	public static final String EXCEPTION_API_VERSION_NOT_SUPPORTED = "FARM_LEDGER_ERR_2011";

	public static final String PLOT_ID_NOT_BLANK = "FARM_LEDGER_ERR_2012";
}
