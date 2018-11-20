package com.devtest.matcher;

import java.util.Comparator;

/***
 * Buffer file queue comparator used when adding items to the PriorityQueue. This will ensure as
 * items are retrieved and added back to the queue they get sorted based on the first number to be
 * read from each file. This ensures sorting from least to most in number order across all files.  
 */
class SplitFileQueueComparator implements Comparator<SplitBufferFileWrapper>
{
	SimpleStringComparator mSimpleNumberComparator = null;
	
	public SplitFileQueueComparator()
	{
		mSimpleNumberComparator = new SimpleStringComparator();		
	}
	
	@Override
	public int compare(SplitBufferFileWrapper file1, SplitBufferFileWrapper file2) {
		return mSimpleNumberComparator.compare(file1.currentLine(), file2.currentLine());
	}
}