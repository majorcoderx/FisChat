package com.fis.server.message;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fis.key.Key;

public class Signal{
	public String type;
	protected JSONParser jsonParser;
	protected JSONObject jsonObj;
	
	public Signal(String jsonString) {
		// TODO Auto-generated constructor stub
		try{
			jsonParser = new JSONParser();
			jsonObj = (JSONObject) jsonParser.parse(jsonString);
			type = (String) jsonObj.get(Key.TYPE);
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
}
