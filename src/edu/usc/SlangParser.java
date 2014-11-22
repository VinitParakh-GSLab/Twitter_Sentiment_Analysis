package edu.usc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SlangParser {
	static HashMap<String, String> slangMap = new HashMap<String, String>();
		
	public static void readSlangFile(){
		BufferedReader bfReader;
		String input = "./preprocessing/SlangsLookUp.txt";
		
		String [] values;
		try {
			bfReader = new BufferedReader( new InputStreamReader(new FileInputStream(input)));	
			String line = "";
			while ((line = bfReader.readLine()) != null) {
				values = line.split("\t");
				slangMap.put(values[0],values[1]);
			}
			
			//printMap(slangMap);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void printMap(HashMap<String, String> map) {
		java.util.Iterator<String> iterator = map.keySet().iterator();  
		int count = 0;   
		while (iterator.hasNext()) {  
		   String key = iterator.next().toString();  
		   String value = map.get(key).toString();  
		   count++;
		   System.out.println(key + "\t\t\t" + value);  
		}  
		System.out.println("count:" + count);
	}
}