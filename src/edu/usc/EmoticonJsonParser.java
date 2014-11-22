package edu.usc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.resource.Resource;

public class EmoticonJsonParser {

	public static HashMap<String, String> map = new HashMap<String, String>();	
	public static Pattern patternEmo;
	
	public static void readJson() {
		String jsonFile = "./preprocessing/kimonoFinal.json";
		JSONObject jsonObj = null;
		BufferedReader reader;
		try {
			
			InputStream stream = Resource.class.getResourceAsStream("kimonoFinal.json");
			BufferedReader bfReader = new BufferedReader( new InputStreamReader(stream));	
			//reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF8"));
			String line = null;
			StringBuffer buffer = new StringBuffer();
			while ((line = bfReader.readLine()) != null) {
				buffer.append(line);
			}
			String jsonString = buffer.toString();
			jsonObj = new JSONObject(jsonString);
			parseJson(jsonObj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return jsonObj;
	}

	private static void parseJson(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		try {			
			JSONObject resultsObj = jsonObj.getJSONObject("results");
			JSONArray collection1 = resultsObj.getJSONArray("collection1");
			
			StringBuilder emoticonPattern = new StringBuilder();
			emoticonPattern.append('(');
			
			for(int i=0; i< collection1.length();i++){
				JSONObject property = collection1.getJSONObject(i);
				String emoString = property.getString("property1");
				//emoString = emoString.replaceAll(" ","HH").trim();
				JSONObject property2 = property.getJSONObject("property2");
				String text = property2.getString("text");
				
				//Process text to remove [2]
				String patternStr = "(\\[[0-9]*\\])";
			    String replaceStr = "";
			    Pattern pattern = Pattern.compile(patternStr);
			    Matcher matcher = pattern.matcher(text);
			    String processedText = matcher.replaceAll(replaceStr);
				String[] emoticons = emoString.split("\\s+");
				
				
				for (String emo : emoticons) {
					//System.out.println(emo);
					emoticonPattern.append(Pattern.quote(emo));
					emoticonPattern.append('|');
					map.put(emo, processedText);
				}
			
			}
			
			emoticonPattern.replace(emoticonPattern.length() - 1, emoticonPattern.length(), ")");
			patternEmo = Pattern.compile(emoticonPattern.toString());
			printMap(map);
			
		} catch (JSONException e) {
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
