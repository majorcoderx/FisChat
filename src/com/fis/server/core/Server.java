package com.fis.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.fis.key.Group;
import com.fis.server.db.ConnectDb;
import com.fis.server.form.SForm;
import com.fis.server.thread.core.ClientThread;

public class Server extends Thread{
	
	private Queue<Socket> queueSocket = new LinkedList<Socket>();
	private Vector<ClientThread> vClient = new Vector<ClientThread>();
	private Vector<String> listAccount = new Vector<String>();
	private Vector<Group> listGroup = new Vector<Group>();
	
	protected ServerSocket serverSocket;
	protected int port;
	
	public Server(int port){
		this.port = port;
	}
	
	public void run(){
		try{
			serverSocket = new ServerSocket(port);
			SForm.textAreaChat.append("**** SERVER OPEN ****\n");
			SForm.openServer = true;
			GetSocket getSocket = new GetSocket(queueSocket, serverSocket);
			getSocket.start();
			new ConnectDb();
			while(true){
				if(queueSocket.size() > 0){
					Socket clientSocket = queueSocket.remove();
					ClientThread clienThread = new ClientThread(clientSocket,vClient,
															listAccount,listGroup);
					vClient.add(0, clienThread);
					vClient.get(0).start();
				}
				else{
					System.out.print("");
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void closeServer(){
		try{
			serverSocket.close();
			vClient.clear();
			listAccount.clear();
			listGroup.clear();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void notifyAllUser(String content) {
		String msg = "{ \"type\" : \"msg\", \"typeMsg\" : \"all\", \"sender\" : \"ADMIN\", \"content\" : \""
				+ content + "\" }";
		try {
			for (int i = 0; i < vClient.size(); ++i) {
				vClient.get(i).osServer.writeUTF(msg + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}




