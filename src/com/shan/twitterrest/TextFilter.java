package com.shan.twitterrest;

public class TextFilter {
	private static TextFilter t = new TextFilter();

	private TextFilter() {
	}

	public static TextFilter getInstance() {
		return t;
	}

	public String process(String text, String ops) {
		String[] commArr = ops.split("");
		for (String comm : commArr) {
			switch (comm) {
			case "1":
				text = removeUrls(text);
				break;
			case "2":
				text = removeHashTags(text);
				break;
			case "3":
				text = removePunctuations(text);
				break;
			}
		}
		return text;
	}

	private String removeUrls(String text) {
		return text.replaceAll("https(.)*", "");
	}

	private String removeHashTags(String text) {
		return text.replaceAll("([@#][a-zA-Z0-9]+[\\s]*)", "");
	}
	
	private String removePunctuations(String text){
		return text.replaceAll("\\p{Punct}+", "");
	}
}
