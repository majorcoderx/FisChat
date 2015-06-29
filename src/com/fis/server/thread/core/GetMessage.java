package com.fis.server.thread.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

public class GetMessage extends Thread {
	
	private Queue<String> queueMessage;
	private Socket clientSocket;
	
	public GetMessage(Socket clientSocket,Queue<String> queueMessage) {
		this.clientSocket = clientSocket;
		this.queueMessage = queueMessage;
	}
	
	public void run(){
		try{
			DataInputStream is = new DataInputStream(clientSocket.getInputStream());
			while(true){
				queueMessage.add(is.readUTF());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
