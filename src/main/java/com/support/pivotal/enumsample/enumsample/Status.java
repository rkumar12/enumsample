package com.support.pivotal.enumsample.enumsample;

public enum Status {
	QA("QA"), UAT("UAT");
	private String str;

	private Status(String str) {
		this.str = str;
	}

	public static Status get(String str) {
		if (QA.str.equalsIgnoreCase(str)) {
			return QA;
		} else if (UAT.str.equalsIgnoreCase(str)) {
			return UAT;
		}
		return QA;
	}
}
