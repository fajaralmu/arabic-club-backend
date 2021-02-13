package com.fajar.arabicclub.constants;

public enum AnswerCode {
	A, B,C, D, E;

	public static AnswerCode parse(String rawAnswerCode) {
		try {
			return valueOf(rawAnswerCode);
		} catch (Exception e) { 
		}
		return null;
	}
}
