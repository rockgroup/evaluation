package com.yocmoon.evaluation.utils;

public class StringUtil {

	public static boolean isNullOrEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static int toInteger(String str, int defaultInt) {
		try {
			defaultInt = Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return defaultInt;
		}
	}
}
