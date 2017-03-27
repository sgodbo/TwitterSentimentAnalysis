package com.shan.twitterrest;

import java.util.ArrayList;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StanfordCoreNLP pipeline;

		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, parse, sentiment");
		props.setProperty("tokenize.whitespace", "true");
		props.setProperty("ssplit.eolonly", "true");
		props.setProperty("parse.maxlen", "20");
		props.setProperty("tokenize.options", "untokenizable=noneDelete");
		pipeline = new StanfordCoreNLP(props);
		
		String tweet = "Ahmedabad is the biggest city of Gujarat."; 
		ArrayList<String> arr = new ArrayList<String>();
		if (tweet != null && tweet.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(tweet);
			StringBuilder sb = new StringBuilder();
			ArrayList<String> tokens = new ArrayList<String>();
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				boolean newToken = true;
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					String posToken = token.get(PartOfSpeechAnnotation.class);
					if (posToken.contains("NNP")) {
						arr.add(token.get(TextAnnotation.class));
					}
				}
			}

		}

		for(String s:arr){
			System.out.println(s);
		}
	}

}
