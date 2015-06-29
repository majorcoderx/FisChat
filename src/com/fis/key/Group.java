package com.fis.key;

import java.util.Vector;

public class Group {
	
	public Group(String idGroup){
		this.idGroup = idGroup;
	}
	
	public String idGroup;
	public String admin;
	public Vector<String> listAccount = new Vector<String>();
}
