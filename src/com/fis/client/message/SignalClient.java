package com.fis.client.message;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SignalClient {
	public String type;
	protected JSONParser jsonParser;
	protected JSONObject jsonObj;
	
	public SignalClient(String jsonString) {
		// TODO Auto-generated constructor stub
		try{
			jsonParser = new JSONParser();
			jsonObj = (JSONObject) jsonParser.parse(jsonString);
			type = (String) jsonObj.get("type");
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
}
