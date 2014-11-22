package edu.usc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.twitter.Extractor;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class StanfordCoreNlpDemo {

  Properties props;
  int countSlang = 0;
  int countSmiley = 0;
  int countTotal = 0;
  
  public static void main(String[] args) throws IOException {
	  
	  //emoticon-parser
	  EmoticonJsonParser.readJson();
	  
	  //slag-parser
	  SlangParser.readSlangFile();
	  
	  StanfordCoreNlpDemo obj = new StanfordCoreNlpDemo();
	  String input = args[0];
	  String output = args[1];
	  obj.readCSV(input, output);
}
  
  //Get Sentiment from Stanford Core NLP
  public String getSentiment(String text)
  {
	  String sentiment = null;
	  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	  
	  Annotation annotation = new Annotation(text);
	  pipeline.annotate(annotation);
	  
	  List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);	  
	  
	  for (CoreMap sentence : sentences) {
	    sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
	  }
	  return sentiment;
  }
  
  //Read the CSV file input
  public void readCSV(String input, String output)
  {
	  props = new Properties();
	  props.setProperty("annotators", "tokenize, ssplit, pos, parse, lemma, stopword, sentiment");
	  props.setProperty("ssplit.isOneSentence", "true");
	  props.setProperty("customAnnotatorClass.stopword", "edu.usc.StopwordAnnotator");
      props.setProperty(StopwordAnnotator.CHECK_LEMMA, "true");
      
	  try {
		  
		  Extractor extractor = new Extractor();
		  CSVWriter writer = new CSVWriter(new OutputStreamWriter(
                  new FileOutputStream(output), "UTF-8"),
                  ',', '"');
		   
		  BufferedReader bfReader = new BufferedReader( new InputStreamReader(new FileInputStream(input)));	
		  CSVReader reader = new CSVReader(bfReader, ',','"','℠');
		  
          try {
	          String[] values = reader.readNext();
	          
	          while (values != null ) {
	        	  countTotal++;
	        	  System.out.println(countTotal);
	        	  
	        	  ArrayList<String> valuesList = new ArrayList<String>(Arrays.asList(values));
	        	  
	        	  //Preprocess the data;
	        	  String replacedString = dataPreProcessing(values[1], extractor);
	        	  replacedString = replacedString.trim();
	        	  
	        	 if(replacedString.length() > 0 && replacedString != null)
	        	  {
	            	  String sentiment = getSentiment(replacedString);
	            	  valuesList.add(sentiment);
	            	  
	            	  String[] sentimentArray = new String[ valuesList.size() ];
	            	  sentimentArray = valuesList.toArray(sentimentArray);
	            	  
	            	  writer.writeNext(sentimentArray);
	        	  }
	        	  
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
	  
	  System.out.println("Slang Tweets: " + countSlang);
	  System.out.println("Smiley Tweets: " + countSmiley);
	  System.out.println("Total Tweets: " + countTotal);
	  System.out.println("Done");
  }
  
  
  public String dataPreProcessing(String input, Extractor extractor)
  {
	  String replacedString = new String(input);
			
	  //remove mentioned tweets
	  List<String> mentionedScreenNames = extractor.extractMentionedScreennames(input);
	  for(int i=0; i < mentionedScreenNames.size(); i++)
	  {
		  replacedString = replacedString.replace("@"+mentionedScreenNames.get(i), "");
	  }
			 
	  //remove screen-name
	  String replyScreenNames = extractor.extractReplyScreenname(input);
	  if(replyScreenNames != null)
		  replacedString = replacedString.replace("@"+replyScreenNames, "");
	  
	  //remove url
	  List<String> extractURL = extractor.extractURLs(input);
	  for(int i=0; i < extractURL.size(); i++)
	  {
		  replacedString = replacedString.replace(extractURL.get(i), "");
	  }
	  
	  replacedString = replacedString.toLowerCase();
	  replacedString = replacedString.trim();
	  
	  	//remove hashtag
	  	replacedString = replacedString.replaceAll("#", "");
	  	
	  	//remove smileys
	  	/*String smileyHat = "[<>]?";
		String smileyEyes = "[:;=8]";
		String smileyNose = "[\\-o\\*\\']?";
		String smileyMouth = "[\\)\\]\\(\\[dDpP/\\:\\}\\{@\\|\\\\]";*/
		
		Matcher smileyMatcher = EmoticonJsonParser.patternEmo.matcher(replacedString);
		int flag = 0;
		while(smileyMatcher.find())
		{
			//To avoid if a sentence contains multiple smileys
			if(flag == 0)
			{
				flag = 1;
				countSmiley++;
			}
			if(EmoticonJsonParser.map.containsKey(smileyMatcher.group()))
				replacedString = replacedString.replace(smileyMatcher.group(), EmoticonJsonParser.map.get(smileyMatcher.group()));
		}
		
		//remove slang-words
		String[] splittedString = replacedString.split("\\s+");
		for(int i=0; i<splittedString.length; i++)
		{
			if(SlangParser.slangMap.containsKey(splittedString[i]))
			{
				String regex = ("\\b"+splittedString[i]+"\\b"); 
				replacedString = replacedString.replaceAll(regex, SlangParser.slangMap.get(splittedString[i]));
				countSlang++;
			}
		}
		
		//replace RT
		replacedString = replacedString.replaceAll("^rt ", "");
		
		//punctuation replace!!
		replacedString = Pattern.compile("(?![!])\\p{Punct}").matcher(replacedString).replaceAll("");
		
		return replacedString;
  }
}