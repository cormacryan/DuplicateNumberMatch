package com.devtest.matcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/***
 * Class helper to wrap a split file when performing the final stage of sorting all the split files. 
 * Specifically used to perform a read line by line and store the current line number for access
 * before we read the next line number.
 * 
 * Used specifically in the PriorityQueue when trying to determine the sorting order of the first item
 * in each file that is the less of all numbers being checked.
 */
public class SplitBufferFileWrapper 
{
    public  static final int BUFFERSIZE = 1024;    
    public  BufferedReader mFileBufferReader;
    public  File mFile;
    private String mNumber;
    private boolean mIsFileEmpty;
    
    /***
     * Constructor to create a new file wrapper for processing.
     * 
     * @param pFile The file that is being wrapped for processing.
     * @throws IOException
     */
    public SplitBufferFileWrapper(File pFile) throws IOException {
    	mFile = pFile;
    	mFileBufferReader = new BufferedReader(new FileReader(pFile), BUFFERSIZE);
    	readNextLine();
    }

    /***
     * Called to read the next line of data for processing and store the retrieved number internally for 
     * later access.
     */
    private void readNextLine()
    {
    	try 
    	{
    		if((this.mNumber = mFileBufferReader.readLine()) == null)
    		{
    			mIsFileEmpty = true;
    			mNumber = null;
    		}
    		else {
    			mIsFileEmpty = false;
    		}
    	} 
        catch(IOException ex) 
        {
        	mIsFileEmpty = true;
        	mNumber = null;
        }
    }
    
    /***
     * Called to return the number stored at the current line in the file we processing.
     *  
     * @return The line number as a string or null if the file is empty or no number exists.
     */
    public String currentLine() {
    	if(mIsFileEmpty) 
    		return null;
    	
    	return mNumber;
    }    
    
    /***
     * Check if the file is empty or we have reached the end of the file while processing numbers.
     * 
     * @return True if the file is empty, False otherwise.
     */
    public boolean isFileEmpty() {
    	return mIsFileEmpty;
    }
            
    /***
     * Called to take the current line number we have retrieved and progress the file buffer pointer
     * to the next line in the file for processing.
     * 
     * @return The current line number before incrementing the file pointer to the next line.
     */
    public String getCurrentAndReadNextLine() {
    	String currentLine = currentLine();
    	readNextLine();
      
    	return currentLine;
    }
    
    /***
     * Perform some cleanup of the file buffer.
     */
    public void close() {
    	try 
    	{
    		mFileBufferReader.close();
    	} 
        catch(IOException ex) {} 
    }    
}
