package com.devtest.matcher;

import java.io.*;
import java.util.*;

/***
 * Application to detect and print out duplicate numbers stored in an input file based on limited memory 
 * and processing available on the machine the application is being run on.  
 * 
 * Input either takes 1 argument which is a file name containing the list of numbers to sort or no arguments
 * in which case a temp file will be created with some random numbers for processing.
 * 
 * Run against a random generated text file of numbers
 * java -jar build/jar/NumberMatcher.jar
 * 
 * Run against a given passed in file of numbers
 * java -jar build/jar/NumberMatcher.jar numbers.txt
 * 
 * Run a test scenario against a small and large number set.
 * java -jar build/jar/NumberMatcher.jar runtest
 */
public class Main 
{
    public static void main(String[] args) 
    {
    	try  
    	{
	    	if (args.length > 0  && args[0].equalsIgnoreCase("runtest"))
	    	{
	    		runTestScenarioA();
	    		runTestScenarioB();
	    	}
	    	else
	    	{
	    		runReleaseScenario(args);
	    	}
    	}
    	catch (IOException ex)
    	{
        	System.out.println("File location or access error occured: " + ex.getMessage());
        	System.exit(0);    		    		
    	}
    }
    
    public static void runReleaseScenario(String[] args) throws IOException
    {
    	String dataFile = "";
        if(args.length > 0) 
        { 
        	dataFile = args[0];
        }
        else
        {
        	dataFile = "numbers.txt";
        	generateRandomTestNumberSet(dataFile, 100000);
        }
        
        NumberController controller = new NumberController(dataFile);
        controller.renderInputNumbers();
        controller.validateAndPrintDuplicateNumbers(false, false);    	
    }
    
    /**
     * Called to run a test scenario against a small subset of numbers.
     * 
     * @throws IOException Error thrown on file access.
     */
    public static void runTestScenarioA() throws IOException
    {
    	String dataFile = "numberstest.txt";
    	
    	// Create a list of duplicate numbers we will ensure appear in the output file
    	String[] dup = new String[] { "8", "9", "144", "325", "438", "9999" };
    	
    	/*
    	 * Create a list of unique numbers along with the duplication list for later validation.
    	 */
    	String[] testNumbers = new String[] 
		{ 
			"325", "144", "8", "9999", "438", "9", "96", "109", "877", "5342", "2", "16",
			dup[0], "11441", "181", "199991", "14381", "191", "1961", "11091", "18771", "153421", "121", "1161",
			"23252", "21442", "282", "299992", dup[1], "292", "2962", "21092", "28772", dup[2], "222", "2162",
			"33253", "31443", dup[3], "399993", "34383", "393", "3963", "31093", "38773", "353423", "323", "3163",
			"43254", dup[4], "484", "499994", "44384", "494", "4964", dup[5], "48774", "453424", "424", "4164",
			"53255", "51445", "585", "599995", "54385", "595", "5965", "51095", "58775", "553425", "525", "5165",
			dup[2], "61446", "686", "699996", "64386", "696", "6966", "61096", "68776", dup[5], "626", "6166",
			"73257", "71447", "787", "799997", dup[3], "797", "7967", "71097", "78777", "753427", "727", "7167"
		};
    	
    	generateSmallTestNumberSet(dataFile, testNumbers);        
    	
        NumberController controller = new NumberController(dataFile);
        controller.renderInputNumbers();
        ArrayList<String> dupResultList = controller.validateAndPrintDuplicateNumbers(true, true);
        
        // Validate the test case to ensure the size of the dupList is the same as the dup string array
        if (dupResultList.size() == dup.length)
        	System.out.println("Test Case A (Correct array size, small number set): Passed");        	
        else
        	System.out.println("Test Case A (Correct array size, small number set): Failed");
        
        /*
         * Validate the test case to ensure the elements in the dup array are contained in the dupList and
         * in the right sort order. 
         */
        boolean failed = false;
        for (int i=0; i<dup.length; i++)
        {
        	if (!dup[i].equals(dupResultList.get(i)))
        	{
        		failed = true;
        		break;
        	}        	
        }
        
        if (!failed)
        	System.out.println("Test Case B (Correct numbers present, small number set): Passed");        	
        else
        	System.out.println("Test Case B (Correct numbers present, small number set): Failed");
    }
    
    /**
     * Called to run a test scenario against a large subset of numbers.
     * 
     * @throws IOException Error thrown on file access.
     */
    public static void runTestScenarioB() throws IOException
    {
    	String dataFile = "numberstest.txt";
    	
    	// Create a list of duplicate numbers we will ensure appear in the output file
    	String[] dup = new String[] { "8", "9", "144", "325", "438", "9999" };
    	
    	generateLargeTestNumberSet(dataFile, dup, 200000);        
    	
        NumberController controller = new NumberController(dataFile);
        controller.renderInputNumbers();
        ArrayList<String> dupResultList = controller.validateAndPrintDuplicateNumbers(true, true);
        
        // Validate the test case to ensure the size of the dupList is the same as the dup string array
        if (dupResultList.size() == dup.length)
        	System.out.println("Test Case C (Correct array size, large number set): Passed");        	
        else
        	System.out.println("Test Case C (Correct array size, large number set): Failed");
        
        /*
         * Validate the test case to ensure the elements in the dup array are contained in the dupList and
         * in the right sort order. 
         */
        boolean failed = false;
        for (int i=0; i<dup.length; i++)
        {
        	if (!dup[i].equals(dupResultList.get(i)))
        	{
        		failed = true;
        		break;
        	}        	
        }
        
        if (!failed)
        	System.out.println("Test Case D (Correct numbers present, large number set): Passed");        	
        else
        	System.out.println("Test Case D (Correct numbers present, large number set): Failed");
    }
        
    /***
     * Helper method to generate and write some array numbers to a text file for processing. All numbers 
     * written will be from the passed in array.
     * 
     * @param pFileName The name of the output file.
     * @param pTestNumbers The array of numbers to add to the output file.
     * 
     * @throws IOException Error thrown on file access.
     */
	public static void generateSmallTestNumberSet(String pFileName, String[] pTestNumbers) throws IOException 
	{
        File numbersFile = new File(pFileName);        
        if (numbersFile.exists())
        	numbersFile.delete();
        numbersFile.createNewFile();
        
        BufferedWriter fileBufferOutputWriter = new BufferedWriter(new FileWriter(numbersFile));
        for (String num : pTestNumbers)
        {
			fileBufferOutputWriter.write(String.valueOf(num));
			fileBufferOutputWriter.newLine();        	        	
        }
        fileBufferOutputWriter.close();		
	}      
	
    /***
     * Helper method to generate and write some numbers to a text file in sequence order from 1 - pMaxCount
     * also provides a string array for adding duplicates.
     *  
     * @param pFileName The name of the output file.
     * @param pDupList Additional list of duplicate numbers
     * @param pMaxCount The max number range from 1 - pMaxCount that will be generated.
     * 
     * @throws IOException Error thrown on file access.
     */
	public static void generateLargeTestNumberSet(String pFileName, String[] pDupList, int pMaxCount) throws IOException 
	{
        File numbersFile = new File(pFileName);        
        if (numbersFile.exists())
        	numbersFile.delete();
        numbersFile.createNewFile();
        
        BufferedWriter fileBufferOutputWriter = new BufferedWriter(new FileWriter(numbersFile));
        
        // Write all the unique numbers to the test file in reverse order to ensure sorting is performed
        for (int i=pMaxCount; i>0; i--)
        {
			fileBufferOutputWriter.write(String.valueOf(i));
			fileBufferOutputWriter.newLine();        	        	
        }
        
        // Write some duplicate numbers to the end of test file
        for (String num : pDupList)
        {
			fileBufferOutputWriter.write(String.valueOf(num));
			fileBufferOutputWriter.newLine();        	        	
        }
        fileBufferOutputWriter.close();		
	} 	
    
    /***
     * Helper method to generate and write some random numbers to a text file for processing. All numbers 
     * will be between 1 - 9999999.
     * 
     * @param pFileName The name of the output file.
     * @param pMaxCount The max number of numbers in the output file.
     * 
     * @throws IOException Error thrown on file access.
     */
	public static void generateRandomTestNumberSet(String pFileName, int pMaxCount) throws IOException 
	{
        Random rnd = new Random();
        File numbersFile = new File(pFileName);        
        if (numbersFile.exists())
        	numbersFile.delete();
        numbersFile.createNewFile();
        
        BufferedWriter fileBufferOutputWriter = new BufferedWriter(new FileWriter(numbersFile));
        for (int i=0; i<pMaxCount; i++)
        {
        	int num = rnd.nextInt(9999999) + 1;
			fileBufferOutputWriter.write(String.valueOf(num));
			fileBufferOutputWriter.newLine();        	
        }
        fileBufferOutputWriter.close();		
	}    
}