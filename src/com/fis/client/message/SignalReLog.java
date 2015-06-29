package com.fis.client.message;

import com.fis.key.Key;

public class SignalReLog extends SignalClient{

	public SignalReLog(String jsonString) {
		super(jsonString);
	}
	
	public boolean getResultLog(){
		String msg = (String) jsonObj.get(Key.RESULT);
		if(msg.equals(Key.TRUE)){
			return true;
		}
		else{
			return false;
		}
	}
}
