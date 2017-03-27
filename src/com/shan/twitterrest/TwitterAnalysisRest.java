package com.shan.twitterrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;

@Path("/twitnlp")
public class TwitterAnalysisRest extends HttpServlet{

	//NLP n1 = NLP.getInstance();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/sentiment")
	public String getMsg(@QueryParam("text") String text) {
		NLP n1 = NLP.getInstance();
		String output = String.valueOf(n1.findSentiment(text));

		return output;

	}

	@GET
	@Path("/ner")
	@Produces(MediaType.TEXT_PLAIN)
	public String getNamedEntities(@QueryParam("text") String text)
			throws IOException {
		NLP n1 = NLP.getInstance();
		ArrayList<String> result = n1.findNamedEntities(text);
		StringBuilder sb = new StringBuilder();
		for (String s : result) {
			sb.append(s);
			sb.append(";");
		}
		String listAsString = "";
		if (sb.length() > 0) {
			listAsString = sb.deleteCharAt(sb.length() - 1).toString();
		}

		return listAsString;
	}

	@POST
	@Path("/pipeline")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRequestedOutputs(InputStream dataStream ,
			@QueryParam("nlpargs") String nlpargs) throws IOException, org.json.simple.parser.ParseException {
		NLP n1 = NLP.getInstance();
		JSONObject jo = new JSONObject();
		String[] commArr = nlpargs.split("");
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(dataStream));
		String temp = "";
		while((temp = br.readLine()) != null){
			sb.append(temp);
		}
		//String[] jsonResponse = n1.processInputAndOutput(sb.toString(), commArr);
		
		
		//return jsonResponse[0];

	}
}
