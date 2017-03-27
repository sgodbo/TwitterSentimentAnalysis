package com.shan.twitterrest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sun.util.logging.resources.logging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterDataFetch {
	final static Logger logger = Logger.getLogger(TwitterDataFetch.class);
	public static List<Status> search(String path, String queryString) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        Properties props = new Properties();
        System.out.println(path);
        InputStream is;
		try {
			is = new FileInputStream(path+"auth_secrets.properties");
			props.load(is);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e2){
			e2.printStackTrace();
			logger.error(e2.getMessage());
		}
        
		System.out.println(props.getProperty("CONSUMER_KEY"));
		System.out.println(props.getProperty("CONSUMER_SECRET"));
		System.out.println(props.getProperty("ACCESS_TOKEN"));
		System.out.println(props.getProperty("ACCESS_SECRET"));
        cb.setDebugEnabled(true).setOAuthConsumerKey(props.getProperty("CONSUMER_KEY"))
                .setOAuthConsumerSecret(props.getProperty("CONSUMER_SECRET"))
                .setOAuthAccessToken(props.getProperty("ACCESS_TOKEN"))
                .setOAuthAccessTokenSecret(props.getProperty("ACCESS_SECRET"));
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query = new Query(queryString);
        query.setCount(100);
        query.setLocale("en");
        query.setLang("en");
        try {
            QueryResult queryResult = twitter.search(query);
            List<Status> allTweets = new ArrayList<Status>();
            List<Status> newTweets = queryResult.getTweets();
            allTweets.addAll(newTweets);
            long oldest = allTweets.get(allTweets.size()-1).getId()-1;
            
            //System.out.println(oldest);
            while(!newTweets.isEmpty()){   
            	query.setMaxId(oldest);
            	newTweets = twitter.search(query).getTweets();
            	allTweets.addAll(newTweets);
            	oldest = allTweets.get(allTweets.size()-1).getId()-1;
            	//System.out.println(oldest);
            }
            return allTweets;
            
        } catch (TwitterException e) {
            // ignore
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return Collections.emptyList();
 
    }	
}
