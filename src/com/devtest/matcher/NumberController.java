package com.devtest.matcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/***
 * Simple wrapper controller class to handle the rendering and validation of the input numbers.
 */
public class NumberController 
{
	// Maximum number of files we will allow to be created for sorting
	private static final int MAXNUMFILES = 512;
	
	// Max available memory allowed to work within when sorting 2MB 
	private static final int MAXMEMORYAVAIL   = 1024*1024*2;
	
	// Name of the final sorted file of numbers that's read to validate duplicates
	private static final String SORTED_OUTPUT_TMPFILE = "sortedOutputFile.tmp";
	
	// The name of the input data file containing the numbers to validate
	private String mInputFile = null;
	
	private SimpleStringComparator mSimpleNumberComparator = null;
	
	/***
	 * Constructor method to create a simple controller class for number processing.
	 * 
	 * @param pInputFile The string name of the file containing the numbers to validate.
	 */
	public NumberController(String pInputFile)
	{
		mInputFile = pInputFile;
		mSimpleNumberComparator = new SimpleStringComparator();
	}
	
	/***
	 * Called to render the input numbers. This method will split the input file into a smaller subset
	 * of files and sort those numbers in each file for later processing. Once the files are sorted and
	 * split the final process will create a new file where all numbers are sorted back ready to be 
	 * validated.
	 * 
	 * @throws IOException Error thrown on file access.
	 */
	public void renderInputNumbers() throws IOException
	{
		ArrayList<File> splitFileListArray = new ArrayList<File>();
        File inputFile = new File(mInputFile);
     
        /***
         * Split and sort the input numbers into separate files
         */
        BufferedReader fileInputReader = new BufferedReader(new FileReader(inputFile));        
        long blocksize = calculatePotentialSplitFileLength(inputFile.length());
        try
        {
        	ArrayList<String> bufferNumberList =  new ArrayList<String>();
            String line = "";
            int fileCountIndex = 0;
            while(line != null) 
            {
                long currentblocksize = 0;
                while((currentblocksize < blocksize) && ((line = fileInputReader.readLine()) != null) )
                {
                	bufferNumberList.add(line);
                    currentblocksize += line.length();
                }
                
                // We've reached the max size we allow per file, sort and save this data
                File sortedFile = sortBufferNumberList(bufferNumberList, fileCountIndex++);
                splitFileListArray.add(sortedFile);
                bufferNumberList.clear();
            }
        } 
        finally {
        	fileInputReader.close();
        }
        
        /***
         * Merge the split files back into a new file where all the numbers are now in a sorted sequence from
         * least to most significant.
         */
        if (splitFileListArray.size() > 0)
        {
            PriorityQueue<SplitBufferFileWrapper> fileQueueManager = 
            		new PriorityQueue<SplitBufferFileWrapper>(8, new SplitFileQueueComparator());
            for (File file : splitFileListArray) {
            	SplitBufferFileWrapper bfb = new SplitBufferFileWrapper(file);
            	fileQueueManager.add(bfb);
            }     
            
            BufferedWriter fileBufferOutputWriter = new BufferedWriter(new FileWriter(SORTED_OUTPUT_TMPFILE));
            try 
            {
            	while(fileQueueManager.size() > 0) 
            	{
            		SplitBufferFileWrapper splitFileWrapper = fileQueueManager.poll();
                    String r = splitFileWrapper.getCurrentAndReadNextLine();
                    fileBufferOutputWriter.write(r);
                    fileBufferOutputWriter.newLine();
                    
                    if(splitFileWrapper.isFileEmpty()) 
                    {
                    	splitFileWrapper.mFileBufferReader.close();
                    	splitFileWrapper.mFile.delete();
                    }                     
                    else {
                    	// Add the wrapper back to the queue so it can be sorted again for the next read.
                    	fileQueueManager.add(splitFileWrapper);
                    }
                }
            } 
            finally { 
            	fileBufferOutputWriter.close();
                for(SplitBufferFileWrapper fileWrapper : fileQueueManager ) 
                	fileWrapper.close();
            }
        }
	}
	
	/***
	 * Called to validate the input numbers which are now in a new temp file sorted. If a sequence of any given number
	 * repeats itself one after the other then a duplicate exists and output this request. This method will only print
	 * out the first occurrence of a matched number once.
	 * 
	 * @param pReturnDuplicateNumbers Boolean to flag if the duplicate numbers should be returned for further processing.
	 * @param pSupressOutput Boolean to flag to indicate if we wish to display the output to the console.
	 * 
	 * @return ArrayList<String> If the input flag is set to true the returned array will contain the matched numbers.
	 * @throws IOException Error reported on file access.
	 */
    public ArrayList<String> validateAndPrintDuplicateNumbers(boolean pReturnDuplicateNumbers, boolean pSupressOutput) throws IOException
    {
    	ArrayList<String> pReturnArray = new ArrayList<String>();    	
		BufferedReader fileBufferReader = new BufferedReader(new FileReader(SORTED_OUTPUT_TMPFILE), 1024);
		
		String prevLine = null;
		String nextLine = null;		
		int prevMatchedNo = -1;
		try 
		{
			while ((nextLine = fileBufferReader.readLine()) != null)
			{
				// Condition to skip the very first read since we've nothing to compare it to
				if (prevLine == null)
				{
					prevLine = nextLine;
					continue;
				}
				else 
				{
					// Check previous line against next line for duplicate match					
					if (prevLine.equals(nextLine))
					{						
						// Simple check to ensure we only print out duplicate message once if more than one entry present 
						int nextMatchedNo = Integer.valueOf(nextLine).intValue();						
						if (prevMatchedNo != nextMatchedNo)
						{
							if (!pSupressOutput)
								System.out.println("Duplicate number found: " + prevLine);
							if (pReturnDuplicateNumbers)
								pReturnArray.add(prevLine);								
							
							prevMatchedNo = Integer.valueOf(prevLine).intValue();
						}
					}
					prevLine = nextLine;
				}
			}
		} 
        finally 
        { 
        	fileBufferReader.close();
    		File sortedOutputFile = new File(SORTED_OUTPUT_TMPFILE);
    		sortedOutputFile.delete();        	
        }
		
		return pReturnArray;
    }
	
    /***
     * Called to perform a simple sort on a list of numbers and to write the new sorted list to a temp
     * output file.
     * 
     * @param bufferNumberList The list of numbers to sort.
     * @param pIndex A unique file index number to help identify sequence of creation and uniqueness.
     * 
     * @return A file object reference to the sorted and created file.
     * @throws IOException Error reported on file access.
     */
    private File sortBufferNumberList(List<String> bufferNumberList, int pIndex) throws IOException  
    {
    	Collections.sort(bufferNumberList, mSimpleNumberComparator);
        File splitFile = File.createTempFile("splitFile_" + String.valueOf(pIndex), ".txt");
        BufferedWriter fileBufferWriter = new BufferedWriter(new FileWriter(splitFile));
        try 
        {
            for(String number : bufferNumberList) 
            {
            	fileBufferWriter.write(number);
            	fileBufferWriter.newLine();
            }
        } 
        finally {
        	fileBufferWriter.close();
        }
        return splitFile;
    }	
	
    /***
     * Helper method to try and determine the best possible size of data for each split file. At the 
     * moment the memory allowed is a fixed size but this can be made more efficient if the memory size
     * of the machine is not known to pull in the current free memory on the machine and make calculations 
     * based on free memory at that time. Disk space may also need to be checked as a considerations if 
     * the potential data input is large.
     * 
     * @param pDataLength The length of the data that will be processed in total.
     * @return Returns the best possible split data size based on machine and memory environment data.
     */
    private long calculatePotentialSplitFileLength(long pDataLength) 
    {    	
        long dataSizePerFile = pDataLength / MAXNUMFILES ;
        if( dataSizePerFile < MAXMEMORYAVAIL)
        {
        	/*
        	 * If the data size comes out to be less, fix it to the amount of memory were allowed to have to 
        	 * reduce the number of files being created.
        	 */
        	dataSizePerFile = MAXMEMORYAVAIL;
        }
        else 
        {
            if(dataSizePerFile >= MAXMEMORYAVAIL) 
            {
            	System.out.println("Potential memory error may occur with amount of data being processed. Unable to proceed.");
            	System.exit(0);
            }
        }
        return dataSizePerFile;
    }
}
