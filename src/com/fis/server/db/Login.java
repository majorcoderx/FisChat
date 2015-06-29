package com.fis.server.db;

import java.sql.SQLException;


public class Login {
	public boolean checkAccount(String acc,String pass){
		boolean check  = false;
		String sql = "select * from account where acc = '"+acc+"' and pass = "+pass;
		try{
			ConnectDb.rs =  ConnectDb.st.executeQuery(sql);
			while(ConnectDb.rs.next()){
				check = true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return check;
	}
}
