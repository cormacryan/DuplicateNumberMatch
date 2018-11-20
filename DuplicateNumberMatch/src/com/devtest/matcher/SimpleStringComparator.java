package com.devtest.matcher;

import java.util.Comparator;

/***
 * Simple string comparator class to compare two string types as numbers for equality.  
 */
class SimpleStringComparator implements Comparator<String>
{
	@Override
	public int compare(String str1, String str2) {
		Integer int1 = Integer.valueOf(str1);
		Integer int2 = Integer.valueOf(str2);
		return int1.compareTo(int2);
	}
}