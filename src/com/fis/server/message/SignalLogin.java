package com.fis.server.message;

import com.fis.key.Key;

public class SignalLogin extends Signal{

	public SignalLogin(String jsonString) {
		super(jsonString);
		// TODO Auto-generated constructor stub
	}

	public String getAccount(){
		return (String) jsonObj.get(Key.ACCOUNT);
	}
	
	public String getPass(){
		return (String) jsonObj.get(Key.PASS);
	}
}
