package com.fis.client.message;

import com.fis.key.Key;

public class SignalMsg extends SignalClient {

	public SignalMsg(String jsonString) {
		super(jsonString);
	}

	public String getTypeMessage() {
		return (String) jsonObj.get(Key.TYPEMSG);
	}

	public String getContent() {
		return (String) jsonObj.get(Key.CONTENT);
	}

	public String getSender() {
		return (String) jsonObj.get(Key.SENDER);
	}

	public String getIdGroup(){
		return (String) jsonObj.get(Key.IDGROUP);
	}
}
