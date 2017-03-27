package com.shan.twitdatarep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import twitter4j.Status;

import com.shan.twitterrest.NLP;
import com.shan.twitterrest.TwitterDataFetch;

/**
 * Servlet implementation class WelcomeFormServlet
 */
public class WelcomeFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	NLP n1 = NLP.getInstance();
	final static private Logger logger = Logger.getLogger(WelcomeFormServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WelcomeFormServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// System.out.println("process initiated");

		/*
		 * if (null != request) {
		 * request.getRequestDispatcher("welcome.jsp").forward(request,
		 * response); }
		 */
		// System.out.println(get);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> paramMap = request.getParameterMap();
		// String query = buildTwitterQuery(paramMap);
		String query = "#sunRail OR ExpandSunRail OR sunRail OR Sunrailriders OR RideSunRail OR #RideSunRail OR #Ridesunrail +exclude:retweets";
		// System.out.println(query);
		logger.debug("query for twitter API ---->" + query);
		long startTime = System.currentTimeMillis();
		String path = getServletContext().getRealPath("/");
		Map<String, String[]> map = request.getParameterMap();
		boolean pythonJavaTweeySwitch = false;
		File f = new File(path + "tweets" + startTime + ".json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));

		List<Object> resp = null;
		if (pythonJavaTweeySwitch)
			resp = n1.processInputAndOutput(
					triggerTweetsFromPython(path, query), "12");
		else
			resp = n1.processInputAndOutput(triggerTweetsFromJava(path, query),
					"12");
		bw.write(resp.get(0).toString());
		bw.close();
		request.setAttribute("data", resp.get(0));
		request.setAttribute("size", resp.get(1));
		request.setAttribute("startTime", startTime);
		request.getRequestDispatcher("/viewgraph").forward(request, response);
	}

	private String buildTwitterQuery(Map<String, String[]> paramMap) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		boolean excludeRTcheck = false;
		for (String key : paramMap.keySet()) {
			if (!key.equals("excludert")) {
				sb.append(paramMap.get(key)[0]);
				sb.append(" ");
				sb.append("OR");
				sb.append(" ");
			} else {
				excludeRTcheck = true;
			}
		}
		if (excludeRTcheck) {
			sb.replace(sb.length() - 3, sb.length() - 1, "+exclude:retweets");
		}

		return sb.toString();
	}

	private List<Status> triggerTweetsFromJava(String path, String query)
			throws IOException {

		TwitterDataFetch t = new TwitterDataFetch();
		return t.search(path, query);
	}

	private String triggerTweetsFromPython(String path, String query)
			throws IOException {
		ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "python \""
				+ path + "Tweepy-Client\\TwitterSentimentParent.py\" False \""
				+ path);
		builder.redirectErrorStream(true);

		Process p = builder.start();
		BufferedReader br1 = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line1 = "";
		StringBuffer sb1 = new StringBuffer();
		Pattern p1 = Pattern.compile("tweets[0-9\\.]+.json");
		String fName = "";
		while ((line1 = br1.readLine()) != null) {
			// sb1.append(line1);
			System.out.println(line1);
			Matcher m = p1.matcher(line1);
			if (m.find()) {
				fName = m.group();
				System.out.println(fName);
				break;
			}
		}
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		BufferedReader br2 = new BufferedReader(new FileReader(new File(path
				+ fName)));
		String line2 = "";
		StringBuffer sb2 = new StringBuffer();
		while ((line2 = br2.readLine()) != null) {
			sb2.append(line2);
		}
		br2.close();

		return sb2.toString();
	}

}
