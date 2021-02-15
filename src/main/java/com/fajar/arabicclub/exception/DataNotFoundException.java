package com.fajar.arabicclub.exception;

public class DataNotFoundException extends RuntimeException{

	public DataNotFoundException(String message) {
		super(ApplicationException.PREFFIX+message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -380643191067882165L;


}
