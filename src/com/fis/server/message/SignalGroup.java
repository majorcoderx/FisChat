package com.fis.server.message;

import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fis.key.Key;

public class SignalGroup extends Signal{

	public SignalGroup(String jsonString) {
		super(jsonString);
	}

	public String getIdGroup(){
		return (String) jsonObj.get(Key.IDGROUP);
	}
	
	public String getAction(){
		return (String) jsonObj.get(Key.ACTION);
	}
	
	public String getAdmin(){
		return (String) jsonObj.get(Key.ADMIN);
	}
	
	public Vector<String> getListAccountGroup(){
		Vector<String> listGr = new Vector<String>();
		JSONArray listArr = (JSONArray) jsonObj.get(Key.NAME);
		for(int i = 0;i < listArr.size() ; ++i){
			JSONObject job = (JSONObject) listArr.get(i);
			listGr.add((String) job.get(Key.ACCOUNT));
		}
		return listGr;
	}
	
	public String getChangeAccount(){
		return (String) jsonObj.get(Key.ACCOUNT);
	}
}
