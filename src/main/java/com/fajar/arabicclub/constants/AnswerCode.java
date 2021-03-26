package com.fajar.arabicclub.constants;

public enum AnswerCode {
	A, B,C, D, E, ESSAY;

	static boolean isExist(String value) {
		AnswerCode[] values = AnswerCode.values();
		for (AnswerCode answerCode : values) {
			if (answerCode.toString().equals(value)) {
				return true;
			}
		}
		return false;
	}
	public static AnswerCode parse(String rawAnswerCode) {
		try {
			if (false == isExist(rawAnswerCode))  {
				return null;
			}
			return valueOf(rawAnswerCode);
		} catch (Exception e) { 
		}
		return null;
	}
}
