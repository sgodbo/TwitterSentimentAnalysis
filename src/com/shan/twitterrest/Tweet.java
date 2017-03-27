package com.shan.twitterrest;

import twitter4j.HashtagEntity;
import twitter4j.URLEntity;

public class Tweet {
	String text;
	String created_at;
	int retweet_count;
	URLEntity[] urls;
	HashtagEntity[] hashtags;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public int getRetweet_count() {
		return retweet_count;
	}
	public void setRetweet_count(int retweet_count) {
		this.retweet_count = retweet_count;
	}
	public URLEntity[] getUrls() {
		return urls;
	}
	public void setUrls(URLEntity[] urls) {
		this.urls = urls;
	}
	public HashtagEntity[] getHashtags() {
		return hashtags;
	}
	public void setHashtags(HashtagEntity[] hashtags) {
		this.hashtags = hashtags;
	}
	
}
