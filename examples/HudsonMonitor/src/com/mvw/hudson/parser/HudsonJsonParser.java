package com.mvw.hudson.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mvw.hudson.model.JobSummary;

public class HudsonJsonParser {

	public static List<JobSummary> parse(String jsonstring) throws JSONException {
		List<JobSummary> list = new ArrayList<JobSummary>();
		JSONObject json = new JSONObject(jsonstring);
		JSONArray jobs = json.getJSONArray("jobs");
		for (int i = 0 ; i < jobs.length(); i++) {
			JobSummary jobSummary = new JobSummary();
			jobSummary.setName(((JSONObject)(jobs.get(i))).getString("name"));
			jobSummary.setUrl(((JSONObject)(jobs.get(i))).getString("url"));
			jobSummary.setColor(((JSONObject)(jobs.get(i))).getString("color"));
			list.add(jobSummary);
		}
		return list;
	}
	
	public static String convertStreamToString(InputStream is) {  
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is),1000);  
	         StringBuilder sb = new StringBuilder(1000);  
	   
	         String line = null;  
	         try {  
	             while ((line = reader.readLine()) != null) {  
	                 sb.append(line + "\n");  
	             }  
	         } catch (IOException e) {  
	             e.printStackTrace();  
	         } finally {  
	             try {  
	                 is.close();  
	             } catch (IOException e) {  
	                 e.printStackTrace();  
	             }  
	         }  
	         return sb.toString();  
	     }  
}
