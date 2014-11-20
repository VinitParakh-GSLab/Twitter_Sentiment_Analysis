package edu.usc;
import java.io.*;
import java.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.util.*;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.CSVReader;
import com.twitter.*;

public class StanfordCoreNlpDemo {

  public static void main(String[] args) throws IOException {
	  StanfordCoreNlpDemo obj = new StanfordCoreNlpDemo();
	  String input = args[0];
	  String output = args[1];
	  obj.readCSV(input, output);
	  
//	  Extractor extractor = new Extractor();
//	  String text = "click here for your #Sachin Tendulkar!! !! #personalized digital autograph.";
//	  List<String> temp = extractor.extractHashtags(text);
//	  obj.getSentiment(text);
  }
  
  public String getSentiment(String text)
  {
	  String sentiment = null;
	  Properties props = new Properties();
	  props.setProperty("annotators", "tokenize, ssplit, pos, parse, lemma, stopword, sentiment");
	  props.setProperty("ssplit.isOneSentence", "true");
	  props.setProperty("customAnnotatorClass.stopword", "StopwordAnnotator");
      props.setProperty(StopwordAnnotator.CHECK_LEMMA, "true");
      
	  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	  
	  Annotation annotation = new Annotation(text);
	  pipeline.annotate(annotation);
	  
	  List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);	  
	  
	  for (CoreMap sentence : sentences) {
		System.out.println(sentence);
	    sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
	    System.out.println(sentiment);
	  }
	  
	  return sentiment;
  }
  
  public void readCSV(String input, String output)
  {
	  try {
		  
		  Extractor extractor = new Extractor();
		  CSVWriter writer = new CSVWriter(new OutputStreamWriter(
                  new FileOutputStream(output), "UTF-8"),
                  ',', '"');
		   
		  BufferedReader bfReader = new BufferedReader( new InputStreamReader(new FileInputStream(input)));	
		  CSVReader reader = new CSVReader(bfReader, ',','"','℠');
		  
          try {
                  
                  String[] values = reader.readNext();
                  int count = 1;
                  
                  while (values != null ) {
                	  
                	  System.out.println(count);
                	  count++;
                	  
                	  ArrayList<String> valuesList = new ArrayList<String>(Arrays.asList(values));
                	  
                	  String replacedString = dataPreProcessing(values[1], extractor);
                	  
                	  //String sentiment = getSentiment(replacedString);
                	  //valuesList.add(sentiment);
                	  
                	  String[] sentimentArray = new String[ valuesList.size() ];
                	  sentimentArray = valuesList.toArray(sentimentArray);
                	  
                	  writer.writeNext(sentimentArray);
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
		  replacedString = replacedString.replace(replyScreenNames, "");
	  
	  //remove url
	  List<String> extractURL = extractor.extractURLs(input);
	  System.out.println(extractURL);
	  
	  for(int i=0; i < extractURL.size(); i++)
	  {
		  replacedString = replacedString.replace(extractURL.get(i), "");
	  }
	  
	  replacedString = replacedString.toLowerCase();
	  replacedString = replacedString.trim();
	  
	  //remove hashtag
	  replacedString = replacedString.replaceAll("#", "");
	  
	  System.out.println(replacedString);
	  return replacedString;
  }
	
}