package com.fis.client.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

import com.fis.client.form.CForm;

public class GetMessage extends Thread{
	
	private Socket clientSocket;
	private Queue<String> queueMessage;
	private DataInputStream is;
	
	public GetMessage(Socket clientSocket,Queue<String> queueMessage){
		this.clientSocket = clientSocket;
		this.queueMessage = queueMessage;
	}
	
	public void run(){
		try{
			is = new DataInputStream(clientSocket.getInputStream());
			while(true){
				queueMessage.add(is.readUTF());
			}
		}catch(IOException e){
			e.printStackTrace();
			CForm.login = false;
			CForm.resetText();
			CForm.listChatForm.clear();
			CForm.listChatGroup.clear();
			CForm.listGroup.removeAll();
			CForm.listOnl.removeAll();
			CForm.textChat.append("Don't connect to host !\n");
		}
	}
}
