package com.fis.client.message;

import com.fis.key.Key;
import com.fis.server.message.Signal;

public class SignalOnline extends Signal{

	public SignalOnline(String jsonString) {
		super(jsonString);
	}
	
	public String getAction(){
		return (String) jsonObj.get(Key.ACTION);
	}
	
	public String getAccount(){
		return (String) jsonObj.get(Key.ACCOUNT);
	}

}
