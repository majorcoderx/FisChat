package com.fis.client.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.fis.client.form.CForm;
import com.fis.client.form.ChForm;
import com.fis.client.form.GForm;
import com.fis.client.message.SignalChatGroup;
import com.fis.client.message.SignalClient;
import com.fis.client.message.SignalMsg;
import com.fis.client.message.SignalOnline;
import com.fis.client.message.SignalReLog;
import com.fis.key.Key;

public class Client extends Thread {
	
	private String host;
	private int port;
	private Socket clientSocket;
	private DataOutputStream os;
	private Queue<String> queueMessage = new LinkedList<String>();
	private String account;
	
	public Client(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(){	
		while (true) {
			if (queueMessage.size() > 0) {
				String msg = queueMessage.remove();
				System.out.println(msg);
				SignalClient signalClient = new SignalClient(msg);
				if (signalClient.type.equals(Key.LOGIN)) {
					resultLogin(msg);
				}
				if (signalClient.type.equals(Key.MESSAGE)) {
					analystMessage(msg);
				}
				if (signalClient.type.equals(Key.ONLINE)) {
					updateListOnline(msg);
				}
				if(signalClient.type.equals(Key.GROUP)){
					analystGroup(msg);
				}
			} else {
				System.out.print("");
			}
		}	
	}
	
	public void analystGroup(String msg){
		SignalChatGroup signalGroup = new SignalChatGroup(msg);
		if(signalGroup.getAction().equals(Key.CREATE)){
			createGroup(signalGroup);
		}
		if(signalGroup.getAction().equals(Key.ADD)){
			addNewAccountGroupChat(signalGroup);
		}
		if(signalGroup.getAction().equals(Key.REMOVE)){
			removeAccountGroupChat(signalGroup);
		}
		if(signalGroup.getAction().equals(Key.DELETE)){
			turnOfGroupChat(signalGroup);
		}
	}
	
	public void turnOfGroupChat(SignalChatGroup signalGroup){
		for(int i = 0 ;i < CForm.listChatGroup.size(); ++i){
			if(CForm.listChatGroup.get(i).idGroup.equals(signalGroup.getIdGroup())){
				CForm.listChatGroup.get(i).frame.setVisible(false);
				CForm.listChatGroup.remove(i);
				CForm.listGroup.remove(signalGroup.getIdGroup());
			}
		}
	}
	
	public void addNewAccountGroupChat(SignalChatGroup signalGroup){
		System.out.println("Add to group");
		for(int i = 0 ;i < CForm.listChatGroup.size(); ++i){
			if(CForm.listChatGroup.get(i).idGroup.equals(signalGroup.getIdGroup())){
				CForm.listChatGroup.get(i).listRecv.add(signalGroup.getChangeAccount());
				CForm.listChatGroup.get(i).cbblistGroup.updateUI();
			}
		}
	}
	
	public void removeAccountGroupChat(SignalChatGroup signalGroup){
		for(int i = 0 ;i < CForm.listChatGroup.size(); ++i){
			if(CForm.listChatGroup.get(i).idGroup.equals(signalGroup.getIdGroup())){
				CForm.listChatGroup.get(i).listRecv.remove(signalGroup.getChangeAccount());
				CForm.listChatGroup.get(i).cbblistGroup.setSelectedIndex(0);
			}
		}
	}
	
	public void createGroup(SignalChatGroup signalGroup){
		boolean checkOpenForm = true;
		for (int i = 0; i < CForm.listChatGroup.size(); ++i) {
			if (CForm.listChatGroup.get(i).idGroup.equals(signalGroup
					.getIdGroup())) {
				checkOpenForm = false;
			}
		}
		if (checkOpenForm) {
			GForm gForm = new GForm(this, signalGroup.getListAccountGroup(),
					account, signalGroup.getAdmin(), signalGroup.getIdGroup());
			CForm.listChatGroup.add(gForm);
			CForm.listGroup.add(signalGroup.getIdGroup());
		}
	}
	
	public void analystMessage(String msg){
		SignalMsg signalMsg = new SignalMsg(msg);
		if(signalMsg.getTypeMessage().equals(Key.ONE)){
			getMessageChatOne(signalMsg);
		}
		if(signalMsg.getTypeMessage().equals(Key.GROUP)){
			getMessageChatGroup(signalMsg);
		}
		if(signalMsg.getTypeMessage().equals(Key.ALL)){
			getMessageChatAll(signalMsg);
		}
	}

	public void getMessageChatOne(SignalMsg signalMsg) {
		boolean openForm = true;
		for (int i = 0; i < CForm.listChatForm.size(); ++i) {
			if(signalMsg.getSender().equals(CForm.listChatForm.get(i).recv)){
				CForm.listChatForm.get(i).textChat.append("<"+signalMsg.getSender()+"> "+signalMsg.getContent()+"\n");
				openForm = false;
				break;
			}
		}
		if(openForm && !signalMsg.getContent().equals("out")){
			ChForm chForm = new ChForm(signalMsg.getSender(), this, account);
			CForm.listChatForm.add(chForm);
			ChForm.textChat.append("<"+signalMsg.getSender()+"> "+signalMsg.getContent()+"\n");
		}
	}
	
	public void getMessageChatGroup(SignalMsg signalMsg) {
		for(int i = 0; i< CForm.listChatGroup.size(); ++i){
			if (CForm.listChatGroup.get(i).idGroup.equals(signalMsg
					.getIdGroup())) {
				if(!CForm.listChatGroup.get(i).frame.isVisible()){
					CForm.listChatGroup.get(i).frame.setVisible(true);
				}
				
				CForm.listChatGroup.get(i).textChat.append("<"
						+ signalMsg.getSender() + "> : "
						+ signalMsg.getContent() + "\n");
			}
		}
	}
	
	public void getMessageChatAll(SignalMsg signalMsg) {
		CForm.textChat.append("<<"+signalMsg.getSender()+">>" + signalMsg.getContent() + "\n");
	}
	
	public void updateListOnline(String msg){
		SignalOnline signalOnline = new SignalOnline(msg);
		if(signalOnline.getAction().equals(Key.ADD)){
			addMemberToListOnline(signalOnline.getAccount());
		}
		else{
			removeMemberToListOnline(signalOnline.getAccount());
		}
	}
	
	public void addMemberToListOnline(String acc){
		CForm.listOnl.add(acc);
		CForm.listOnlGr.add(acc);
	}
	
	public void removeMemberToListOnline(String acc){
		CForm.listOnl.remove(acc);
		CForm.listOnlGr.remove(acc);
	}
	
	public void sendLogin(String acc, String pass){
		try{
			clientSocket = new Socket(host, port);
			os = new DataOutputStream(clientSocket.getOutputStream());
			GetMessage getMessage = new GetMessage(clientSocket, queueMessage);
			getMessage.start();
			String msg = "{ \"type\" : \"log\", \"acc\" : \""+acc+"\", \"pass\":\""+pass+"\" }";
			os.writeUTF(msg + "\r\n");
			account = acc;
		}catch(IOException e){
			e.printStackTrace();
			CForm.textChat.append("[Connect] : check your host or post !\n");
		}
	}
	
	public void resultLogin(String msg){
		SignalReLog signalRelog = new SignalReLog(msg);
		if(signalRelog.getResultLog()){
			CForm.login = true;
			CForm.textChat.append("*** WELCOME TO FIS CHAT ***\n");
		}
		else{
			CForm.login = false;
			CForm.textChat.append("*** LOGIN FAIL ! ****\n");
		}
	}
	
	public void sendMsgOne(String content, String recv, String sender) {
		try {
			String msg = "{ \"type\" : \"msg\", \"typeMsg\" : \"one\", \"sender\" : \""
					+ sender
					+ "\", \"recv\" : \""
					+ recv
					+ "\", \"content\" : \"" + content + "\" }";
			os.writeUTF(msg + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void sendMsgAll(String content, String sender) {
		try{
			String msg = "{ \"type\" : \"msg\", \"typeMsg\" : \"all\", \"sender\" : \""
					+ sender + "\", \"content\" : \"" + content + "\" }";
			os.writeUTF(msg + "\r\n");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendMsgGroup(String content, String sender, String idGroup) {
		String msg = "{ \"type\" : \"msg\", \"typeMsg\" : \"gr\", \"idGroup\" : \""
				+ idGroup
				+ "\", \"sender\" : \""
				+ sender
				+ "\", \"content\" : \"" + content + "\" }";
		try {
			os.writeUTF(msg + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCreateGroup(String idGroup,Vector<String> listAcc, String admin){
		String msg = "{ \"type\" : \"gr\", \"idGroup\" : \"" + idGroup
				+ "\", \"action\" : \"create\", \"admin\" : \"" + admin + "\", \"name\" : [ ";
		for (int i = 0; i < listAcc.size(); ++i) {
			if (i != listAcc.size() - 1) {
				msg += "{ \"acc\" : \"" + listAcc.get(i) + "\" },";
			} else {
				msg += "{ \"acc\" : \"" + listAcc.get(i) + "\" }";
			}
		}
		msg += " ] }";
		try{
			os.writeUTF(msg + "\r\n");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void addAccountGroup(String idGroup, String acc) {
		String msg = "{ \"type\" : \"gr\", \"idGroup\" : \"" + idGroup
				+ "\", \"action\" : \"add\", \"acc\" : \"" + acc + "\" }";
		try {
			os.writeUTF(msg + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeAccountGroup(String idGroup, String acc) {
		String msg = "{ \"type\" : \"gr\", \"idGroup\" : \"" + idGroup
				+ "\", \"action\" : \"remove\", \"acc\" : \"" + acc + "\" }";
		try {
			os.writeUTF(msg + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void turnOfGroup(String idGroup) {
		String msg = "{ \"type\" : \"gr\", \"idGroup\" : \"" + idGroup
				+ "\", \"action\" : \"del\" }";
		try {
			os.writeUTF(msg + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logoutSystem(){
		try{
			String msg = "{ \"type\" : \"out\"}";
			os.writeUTF(msg + "\r\n");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
