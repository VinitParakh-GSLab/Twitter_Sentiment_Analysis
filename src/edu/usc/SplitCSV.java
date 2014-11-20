package edu.usc;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.CSVReader;


public class SplitCSV {

	public static void main(String [] args)
	{
		SplitCSV obj = new SplitCSV();
		obj.readCSV("/media/Android/tweets.csv");
	}
	
	public void readCSV(String input)
	  {
		  try {
			   
			  CSVWriter writer = new CSVWriter(new OutputStreamWriter(
	                  new FileOutputStream("/media/Android/tweets0.csv"), "UTF-8"),
	                  ',', '\"');
			  
	          CSVReader reader = new CSVReader(new InputStreamReader(
	                          new FileInputStream(input), "UTF-8"), 
	                          ',', '\"', 0);  
	          try {
	                  
	                  String[] values = reader.readNext();
	                  int count = 0;
	                  
	                	  while (values != null ) {
	                		  if(count%8198122 == 0 && count != 0)
	    	                  {	  
	                			  writer.close();
	                			  System.out.println("here");
	                			  int fileNum = count/8198122;
	                			  String output = "/media/Android/tweets" + String.valueOf(fileNum) + ".csv";
	                			  
	                			  System.out.println(output);
	                			  writer = new CSVWriter(new OutputStreamWriter(
	    	        	                  new FileOutputStream(output), "UTF-8"),
	    	        	                  ',', '\"');
	    	                  }
	                		  
	                	  System.out.println(count);
	                	  count++;
	                	  
	                	  writer.writeNext(values);
	                	  values = reader.readNext();
	                	  }
	          } finally {
	                  // we have to close reader manually
	                  reader.close();
	                  writer.close();
	          }
		  } catch (IOException e) {
	          // we have to process exceptions when it is not required
	          e.printStackTrace();
		  }	
		  System.out.println("Done");
	  }
}
