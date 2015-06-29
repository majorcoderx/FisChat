package com.fis.server.message;

import com.fis.key.Key;

public class SignalMessage extends Signal{

	public SignalMessage(String jsonString) {
		super(jsonString);
		// TODO Auto-generated constructor stub
	}

	public String getTypeMessage(){
		return (String) jsonObj.get(Key.TYPEMSG);
	}
	
	public String getContent(){
		return (String) jsonObj.get(Key.CONTENT);
	}
	
	public String getSender(){
		return (String) jsonObj.get(Key.SENDER);
	}
	
	public String getRecv(){
		return (String) jsonObj.get(Key.RECV);
	}
	
	public String getIdGroup(){
		return (String) jsonObj.get(Key.IDGROUP);
	}
}
