package com.fajar.arabicclub.exception;

public class ApplicationException extends RuntimeException{

	public static final String PREFFIX = "#_APP_EXCEPTION_#";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7610558300205998680L;

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Exception ex) {
		super(ex.getMessage());
	}
  
}
