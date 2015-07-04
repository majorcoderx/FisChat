package com.fis.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class GetSocket extends Thread {
	

	private ServerSocket serverSocket;
	private Queue<Socket> queueSocket;
	
	public GetSocket(Queue<Socket> queueSocket,ServerSocket serverSocket){
		this.serverSocket = serverSocket;
		this.queueSocket = queueSocket;
		
	}
	
	public void run(){
		try{
			while(true){
				queueSocket.add(serverSocket.accept());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
