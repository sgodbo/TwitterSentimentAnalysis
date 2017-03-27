package com.shan.twitterrest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

public class NLP {
	static StanfordCoreNLP pipeline;

	static Properties props = new Properties();
	private static NLP n1 = new NLP();
	final static private Logger logger = Logger.getLogger(NLP.class);

	private NLP() {
		init();
	}

	public static NLP getInstance() {
		return n1;
	}

	@SuppressWarnings("unchecked")
	public List<Object> processInputAndOutput(Object args, String command) {
		JSONArray jArr = null;
		TextFilter filter = TextFilter.getInstance();
		TreeSet<String> indiTweets = new TreeSet<String>();
		try {
			if (args instanceof String) {
				String jsonString;
				jsonString = (String) args;
				//System.out.println(jsonString);
				JSONParser jp = new JSONParser();
				Object obj = jp.parse(jsonString);
				JSONObject jPre = (JSONObject) obj;
				jArr = (JSONArray) jPre.get("tweetObj");
			} else {
				jArr = new JSONArray();

				for (Status s : (List<Status>) args) {
					if (!indiTweets.contains(s.getText())) {
						JSONObject jObj = new JSONObject();
						jObj.put("text", filter.process(s.getText(), "123"));
						jObj.put("created_at", s.getCreatedAt().toString());
						jObj.put("retweet_count", s.getRetweetCount());
						jObj.put("tweet_id", s.getId());

						HashtagEntity[] tempHashtags = s.getHashtagEntities();
						ArrayList<String> hashtags = new ArrayList<String>();
						for (HashtagEntity h : tempHashtags) {
							hashtags.add(h.getText());
						}
						jObj.put("hashtags", hashtags);
						URLEntity[] tempUrls = s.getURLEntities();
						ArrayList<String> urls = new ArrayList<String>();
						for (URLEntity u : tempUrls) {
							urls.add(u.getDisplayURL());
						}
						jObj.put("urls", urls);
						jArr.add(jObj);
						indiTweets.add(s.getText());
					}
				}
			}
		} catch (ParseException pe) {
			logger.error(pe.getMessage());
		}
		return processOutput(jArr, command);
	}

	@SuppressWarnings("unchecked")
	public List<Object> processOutput(JSONArray jArr, String command) {
		JSONObject jPre = new JSONObject();
		Map<String, float[]> mapTagSent = new HashMap<String, float[]>();
		JSONArray newJArr = new JSONArray();
		ExecutorService executor = Executors.newFixedThreadPool(20);
		Map<String, float[]> topTenSent = new HashMap<String, float[]>();
		try {
			int tweetsPerThread = jArr.size() / 20;
			class MyThread implements Callable<List<Object>> {
				int threadId;
				JSONArray jArr;
				Map<String, float[]> tempMapTagSent;
				int tweetsPerThread;
				String command;

				public MyThread(int i, int div, JSONArray jsonArray,
						Map<String, float[]> topTenSent, String comm) {
					threadId = i;
					jArr = jsonArray;
					tempMapTagSent = topTenSent;
					tweetsPerThread = div;
					command = comm;
				}

				@Override
				public List<Object> call() {
					// TODO Auto-generated method stub
					List<Object> listOfResults = new ArrayList<Object>();
					int start = threadId * tweetsPerThread;
					int end = 0;
					if (threadId == 19) {
						end = jArr.size() - 1;
					} else {
						end = (threadId + 1) * tweetsPerThread;
					}
					for (int idx = start; idx < end; idx++) {

						JSONObject jObj = (JSONObject) jArr.get(idx);
						switch (command) {
						case "1":
							jObj.put("sentimentScore",
									n1.findSentiment((String) jObj.get("text")));
							break;

						case "2":
							ArrayList<String> hashtags = (ArrayList<String>) jObj
									.get("hashtags");
							ArrayList<String> entities = new ArrayList<String>();
							if (hashtags.isEmpty())
								entities = n1.findNamedEntities((String) jObj
										.get("text"));
							jObj.put("entities", entities);
							break;

						case "12":
							// System.out.println("inside thread number "+threadId);

							hashtags = (ArrayList<String>) jObj.get("hashtags");
							List<Object> results = n1.findSentimentAndEntities(
									(String) jObj.get("text"),
									hashtags.isEmpty());
							jObj.put("sentimentScore", results.get(0));
							entities = (ArrayList<String>) results.get(1);
							jObj.put("entities", entities);
							for (String h : hashtags) {
								if (tempMapTagSent.containsKey(h)) {
									float[] arr = tempMapTagSent.get(h);
									arr[1] = (arr[1] * arr[0] + (int) results
											.get(0)) / (arr[0] + 1);
									arr[0]++;
									tempMapTagSent.put(h, arr);
								} else {
									float[] arr = new float[2];
									arr[1] = (int) results.get(0);
									arr[0] = 1;
									tempMapTagSent.put(h, arr);
								}
							}
							break;

						}
						jArr.set(idx, jObj);
					}

					listOfResults.add(tempMapTagSent);
					listOfResults.add(jArr);
					return listOfResults;
				}

			}

			Future<List<Object>> results = null;
			JSONObject adj1 = new JSONObject();
			adj1.put("sentimentScore", 6);
			adj1.put("entities", new ArrayList<String>());
			adj1.put("hashtags", new ArrayList<String>());

			JSONObject adj2 = new JSONObject();
			adj2.put("sentimentScore", -1);
			adj2.put("entities", new ArrayList<String>());
			adj2.put("hashtags", new ArrayList<String>());

			newJArr.add(newJArr.size(), adj1);
			newJArr.add(newJArr.size(), adj2);

			for (int i = 0; i < 20; i++) {
				Callable<List<Object>> worker = new MyThread(i,
						tweetsPerThread, jArr, topTenSent, command);
				results = executor.submit(worker);
				newJArr.addAll((JSONArray) results.get().get(1));
				mapTagSent.putAll((Map<String, float[]>) results.get().get(0));
			}
			executor.shutdown();
		} catch (InterruptedException ie) {
			logger.info(ie.getMessage());
		} catch (ExecutionException ee) {
			logger.info(ee.getMessage());
		}
		logger.debug("size of maptagsent -> " + mapTagSent.size());
		jPre.put("tweetObj", newJArr);

		JSONObject mapTagJson = new JSONObject();
		Comparator<String> comparator = new ValueComparator(mapTagSent);
		TreeMap<String, Float> sortedMapTagSent = new TreeMap<String, Float>(
				comparator);
		for (String key : mapTagSent.keySet()) {
			sortedMapTagSent.put(key, mapTagSent.get(key)[1]);
		}
		logger.debug("size of treemap -> " + sortedMapTagSent.size());
		List<String> top5Entities = new ArrayList<String>();
		for(String key:sortedMapTagSent.keySet()){
			top5Entities.add(key);
			if(top5Entities.size() == 5)
				break;
		}
		System.out.println("size of top 5 entities ---->" + top5Entities.size());
		
		Map<String, Float> finalTop5Map = new LinkedHashMap<String, Float>();
		for(String s:top5Entities){
			finalTop5Map.put(s, sortedMapTagSent.get(s));
		}
		mapTagJson.put("Top10EntitySents", finalTop5Map);
		jPre.put("mapTagSent", mapTagJson);
		List<Object> arr = new ArrayList<Object>();
		arr.add(jPre.toJSONString());
		arr.add(String.valueOf(jArr.size()));

		return arr;

	}

	public static void init() {
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		props.setProperty("tokenize.whitespace", "true");
		props.setProperty("ssplit.eolonly", "true");
		props.setProperty("parse.maxlen", "20");
		props.setProperty("tokenize.options", "untokenizable=noneDelete");
		// System.out.println(NLP.class.getClassLoader().getResource("").getPath());
		pipeline = new StanfordCoreNLP(props);
	}

	public ArrayList<String> findNamedEntities(String tweet) {
		ArrayList<String> arr = new ArrayList<String>();
		if (tweet != null && tweet.length() > 0) {
			Annotation annotation = pipeline.process(tweet);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					String posToken = token.get(PartOfSpeechAnnotation.class);
					if (posToken.contains("NNP")) {
						arr.add(token.get(TextAnnotation.class));
					}
				}
			}

		}

		return arr;
	}

	public int findSentiment(String tweet) {

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

	public ArrayList<Object> findSentimentAndEntities(String tweet,
			boolean entitySwitch) {
		int mainSentiment = 0;
		ArrayList<Object> finalArr = new ArrayList<Object>();
		ArrayList<String> arr = new ArrayList<String>();
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
				if (entitySwitch) {
					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
						String posToken = token
								.get(PartOfSpeechAnnotation.class);
						if (posToken.contains("NNP")) {
							arr.add(token.get(TextAnnotation.class));
						}
					}
				}
			}
		}
		finalArr.add(mainSentiment);
		finalArr.add(arr);
		return finalArr;
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Float> map = new LinkedHashMap<String, Float>();
	Map<String, float[]> refMap = new LinkedHashMap<String, float[]>();

	public ValueComparator(Map<String, float[]> map) {
		refMap = map;
		for (String key : map.keySet()) {
			this.map.put(key, map.get(key)[1]);
		}
	}

	@Override
	public int compare(String o1, String o2) {

			if (refMap.get(o1)[0] >= refMap.get(o2)[0]) {
				return -1;
			} else {
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
