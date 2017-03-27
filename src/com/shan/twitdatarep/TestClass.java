package com.shan.twitdatarep;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import twitter4j.Status;

import com.shan.twitterrest.TwitterDataFetch;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class TestClass {
	
	static StanfordCoreNLP pipeline;
	static List<Integer> l;
	static Properties props = new Properties();
	public static void test(String path) throws IOException, InterruptedException{
		 MaxentTagger tagger = new MaxentTagger(
	                "english-bidirectional-distsim.tagger");
	 
	        // The sample string
	        String sample = "This is a sample text";
	 
	        // The tagged string
	        String tagged = tagger.tagString(sample);
	 
	        // Output the result
	        System.out.println(tagged);
	    
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		//String[] cmd = {"cmd", "/c", "python Tweepy-Client/TwitterSentimentParent.py False", "/c"};///../../../Tweepy-Client","python TwitterSentimentParent.py False"};
		//ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "python Tweepy-Client/Test-Class.py");
		//ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "python Tweepy-Client/TwitterSentimentParent.py False");
		//TestClass t1 = new TestClass();
		//test("fsdfds");
		//System.out.println(findSentiment("I am feeling so high"));
		/*TwitterDataFetch t = new TwitterDataFetch();
		List<Status> l = t.search("");
		System.out.println(l.size());
		for(Status s:l){
			System.out.println(s.getText());
		}*/
		/*Map<String, int[]> toSortMap = new TreeMap<String, int[]>();
		for(int i = 0;i < 5;i++){
			int[] arr = new int[2];
			arr[0] = i;
			arr[1] = i+1;
			String s = "hello" + String.valueOf(i);
			toSortMap.put(s, arr);
		}
		Comparator<String> comparator = new ValueComparator(toSortMap);
		//TreeMap is a map sorted by its keys. 
		//The comparator is used to sort the TreeMap by keys. 
		TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
		for(String s:toSortMap.keySet()){
			result.put(s, toSortMap.get(s)[1]);
		}
		System.out.println(result.size());
		/*for(Entry<String, int[]> entry:result.entrySet()){
			System.out.println(entry.getKey());
			int[] temparr = entry.getValue();
		}

		JSONObject jo = new JSONObject();
		jo.put("arr", result);
		System.out.println(jo.toJSONString());
		l = new ArrayList<Integer>();
		for(int i = 0;i < 10;i++){
			l.add(i);
		}
		ExecutorService executor = Executors.newFixedThreadPool(5);
		class MyThread implements Runnable{

			int id;
			public MyThread(int i) {
				// TODO Auto-generated constructor stub
				id = i;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println(String.valueOf(l.get(id)) + "----> thread number " + id);
				System.out.println(String.valueOf(l.get(id+1)) + "----> thread number " + id);
			}
			
		}
		for (int i = 0; i < 5; i++) {
			Runnable worker = new MyThread(i);
			executor.execute(worker);
		}
		
		executor.shutdown();*/
		
		System.out.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;

		try {
			String conn = "jdbc:mysql://127.5.44.2:3306/tweetpolarity?user=adminCVnN51d&password=wkHUaGeYRcQn&useUnicode=true&characterEncoding=UTF-8";
			connection = DriverManager
			.getConnection(conn);

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
	}
	
	
	public static TreeMap<String, int[]> sortMapByValue(Map<String, int[]> map){
		Comparator<String> comparator = new ValueComparator(map);
		//TreeMap is a map sorted by its keys. 
		//The comparator is used to sort the TreeMap by keys. 
		TreeMap<String, int[]> result = new TreeMap<String, int[]>(comparator);
		result.putAll(map);
		return result;
	}
	
	
	
	public static int findSentiment(String tweet) {

		props.setProperty("annotators",
				"tokenize, ssplit, parse, sentiment");
		props.setProperty("tokenize.whitespace", "true");
		props.setProperty("ssplit.eolonly", "true");
		props.setProperty("parse.maxlen", "20");
		props.setProperty("tokenize.options", "untokenizable=noneDelete");
		pipeline = new StanfordCoreNLP(props);
		
		int mainSentiment = 0;
		if (tweet != null && tweet.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(tweet);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}

			}
		}
		return mainSentiment;
	}

}

class ValueComparator implements Comparator<String>{
	 
	Map<String, Integer> map = new HashMap<String, Integer>();
 
	public ValueComparator(Map<String, int[]> map){
		for(String k:map.keySet()){
			this.map.put(k, map.get(k)[1]);
		}
	}
 
	@Override
	public int compare(String s1, String s2) {
		if(map.get(s1) >= map.get(s2)){
			return -1;
		}else{
			return 1;
		}	
	}
	@Override
	public Comparator<String> reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<String> thenComparing(Comparator<? super String> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> Comparator<String> thenComparing(
			Function<? super String, ? extends U> keyExtractor,
			Comparator<? super U> keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends Comparable<? super U>> Comparator<String> thenComparing(
			Function<? super String, ? extends U> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<String> thenComparingInt(
			ToIntFunction<? super String> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<String> thenComparingLong(
			ToLongFunction<? super String> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<String> thenComparingDouble(
			ToDoubleFunction<? super String> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static <T> Comparator<T> nullsFirst(Comparator<? super T> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> nullsLast(Comparator<? super T> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T, U> Comparator<T> comparing(
			Function<? super T, ? extends U> keyExtractor,
			Comparator<? super U> keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
			Function<? super T, ? extends U> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingInt(
			ToIntFunction<? super T> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static <T> Comparator<T> comparingLong(
			ToLongFunction<? super T> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingDouble(
			ToDoubleFunction<? super T> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}	
}
