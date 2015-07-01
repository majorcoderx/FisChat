package com.fis.server.thread.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.fis.key.Group;
import com.fis.key.Key;
import com.fis.server.db.Login;
import com.fis.server.form.SForm;
import com.fis.server.message.Signal;
import com.fis.server.message.SignalGroup;
import com.fis.server.message.SignalLogin;
import com.fis.server.message.SignalMessage;


public class ClientThread extends Thread{
	
	public String account = "";
	
	private Socket clientSocket;
	private Queue<String> queueMessage = new LinkedList<String>();
	private Vector<ClientThread> vClient;
	private Vector<String> listAccount;
	private Vector<Group> listGroup;
	
	private DataOutputStream os;
	public DataOutputStream osServer = null;
	
	public ClientThread(Socket clientSocket,Vector<ClientThread> vClient,
						Vector<String> listAccount, Vector<Group> listGroup){
		this.clientSocket = clientSocket;
		this.vClient = vClient;
		this.listAccount = listAccount;
		this.listGroup = listGroup;
	}
	
	public void run(){
		try{
			GetMessage getMessage = new GetMessage(clientSocket,queueMessage);
			getMessage.start();
			os = new DataOutputStream(clientSocket.getOutputStream());
			osServer = os;
			while(true){
				if(queueMessage.size() > 0){
					String msg = queueMessage.remove();
					System.out.println(msg);
					Signal signal = new Signal(msg);
					if (signal.type.equals(Key.LOGIN)) {
						sendResultLogin(msg);
					}
					if (signal.type.equals(Key.LOGOUT)) {
						logoutAllGroup();
						updateRemoveListOnline();
						getMessage.interrupt();
						this.clientSocket.close();
						this.interrupt();
					}
					if (signal.type.equals(Key.GROUP)) {
						analystGroupSignal(msg);
					}
					if (signal.type.equals(Key.MESSAGE)) {
						analystMessage(msg);
					}
				} else
					System.out.print("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logoutAllGroup() throws IOException {
		System.out.println(account);
		for (int i = 0; i < listGroup.size(); ++i) {
			System.out.println(listGroup.get(i).admin);
			if(listGroup.get(i).admin.equals(account)){
				//del group
				deleteGroup(listGroup.get(i).idGroup);
			}
			else{
				for (int j = 0; j < listGroup.get(i).listAccount.size(); ++j) {
					if (listGroup.get(i).listAccount.get(j).equals(account)) {
						sendChangeAccount(listGroup.get(i), account, Key.REMOVE);
					}
				}
			}
		}
	}
	
	public void analystGroupSignal(String msg) throws IOException{
		SignalGroup signalGroup = new SignalGroup(msg);
		if(signalGroup.getAction().equals(Key.CREATE)){
			boolean checkCreate = true;
			for(int i = 0; i < listGroup.size(); ++i){
				if(listGroup.get(i).idGroup.equals(signalGroup.getIdGroup())){
					checkCreate = false;
				}
			}
			if(checkCreate){
				createNewGroup(signalGroup);
			}
		}
		if(signalGroup.getAction().equals(Key.ADD)){
			addNewAccountGroup(signalGroup.getIdGroup(), signalGroup.getChangeAccount());
		}
		if(signalGroup.getAction().equals(Key.REMOVE)){
			removeAccountGroup(signalGroup.getIdGroup(), signalGroup.getChangeAccount());
		}
		if(signalGroup.getAction().equals(Key.DELETE)){
			deleteGroup(signalGroup.getIdGroup());
		}
	}
	
	public void createNewGroup(SignalGroup signalGroup) throws IOException{
		Group group = new Group(signalGroup.getIdGroup());
		group.admin = signalGroup.getAdmin();
		group.listAccount = signalGroup.getListAccountGroup();
		listGroup.add(group);
		sendInfoGroup(group);
		SForm.listGroup.add(group.idGroup);
	}
	
	public void addNewAccountGroup(String idGroup, String account)
			throws IOException {
		for (int i = 0; i < listGroup.size(); ++i) {
			if (listGroup.get(i).idGroup.equals(idGroup)) {
				sendChangeAccount(listGroup.get(i), account, Key.ADD);
				listGroup.get(i).listAccount.add(account);
				for (int j = 0; j < vClient.size(); ++j) {
					if (vClient.get(j).account.equals(account)) {
						String msg = "{ \"type\" : \"gr\", \"idGroup\": \""
								+ idGroup
								+ "\", \"action\" : \"create\", \"admin\" : \""
								+ listGroup.get(i).admin + "\", \"name\" : [ ";
						for (int k = 0; k < listGroup.get(i).listAccount.size(); ++k) {
							if (k != listGroup.get(i).listAccount.size() - 1) {
								msg += "{ \"acc\" : \""
										+ listGroup.get(i).listAccount.get(k)
										+ "\" },";
							} else {
								msg += "{ \"acc\" : \""
										+ listGroup.get(i).listAccount.get(k)
										+ "\" }";
							}
						}
						msg += " ] }";
						vClient.get(j).os.writeUTF(msg + "\r\n");
					}
				}
			}
		}
	}
	
	public void sendChangeAccount(Group group, String account, String action)
			throws IOException {
		String msg = "{ \"type\" : \"gr\", \"idGroup\" : \"" + group.idGroup
				+ "\", \"action\" : \"" + action + "\", \"acc\" : \"" + account
				+ "\" }";
		for (int i = 0; i < vClient.size(); ++i) {
			for (int j = 0; j < group.listAccount.size(); ++j) {
				if (vClient.get(i).account.equals(group.listAccount.get(j))
						&& !vClient.get(i).account.equals(account)) {
					vClient.get(i).os.writeUTF(msg + "\r\n");
				}
			}
		}
	}
	
	public void removeAccountGroup(String idGroup, String account) throws IOException{
		for(int i = 0;i < listGroup.size(); ++i){
			if(listGroup.get(i).idGroup.equals(idGroup)){
				senDelGroupAccount(listGroup.get(i), account);
				listGroup.get(i).listAccount.remove(account);
				sendChangeAccount(listGroup.get(i), account, Key.REMOVE);
			}
		}
	}
	
	public void senDelGroupAccount(Group group, String account) throws IOException{
		for(int i = 0 ; i < vClient.size(); ++i){
			if(vClient.get(i).account.equals(account)){
				String msg = "{ \"type\" : \"gr\", \"idGroup\" : \""
						+ group.idGroup + "\", \"action\" : \"del\" }";
				vClient.get(i).os.writeUTF(msg + "\r\n");
			}
		}
	}
	
	public void sendInfoGroup(Group group) throws IOException {
		String msg = "{ \"type\" : \"gr\", \"idGroup\": \"" + group.idGroup
				+ "\", \"action\" : \"create\", \"admin\" : \"" + group.admin
				+ "\", \"name\" : [ ";
		for (int i = 0; i < group.listAccount.size(); ++i) {
			if (i != group.listAccount.size() - 1) {
				msg += "{ \"acc\" : \"" + group.listAccount.get(i) + "\" },";
			} else {
				msg += "{ \"acc\" : \"" + group.listAccount.get(i) + "\" }";
			}
		}
		msg += " ] }";
		for (int i = 0; i < vClient.size(); ++i) {
			for (int j = 0; j < group.listAccount.size(); ++j) {
				if (vClient.get(i).account.equals(group.listAccount.get(j))
						&& !vClient.get(i).account.equals(group.admin)) {
					vClient.get(i).os.writeUTF(msg + "\r\n");
				}
			}
		}
	}
	
	public void deleteGroup(String idGroup) throws IOException{
		for(int i = 0 ; i < listGroup.size(); ++i){
			if(listGroup.get(i).idGroup.equals(idGroup)){
				sendDelgroup(listGroup.get(i));
				listGroup.remove(i);
				SForm.listGroup.remove(idGroup);
			}
		}
	}
	
	public void sendDelgroup(Group group) throws IOException {
		for (int i = 0; i < vClient.size(); ++i) {
			for (int j = 0; j < group.listAccount.size(); ++j) {
				if (vClient.get(i).account.equals(group.listAccount.get(j))) {
					String msg = "{ \"type\" : \"gr\", \"idGroup\" : \""
							+ group.idGroup + "\", \"action\" : \"del\" }";
					if(!vClient.get(i).account.equals(account))
						vClient.get(i).os.writeUTF(msg+"\r\n");
				}
			}
		}
	}
	
	public void analystMessage(String msg) throws IOException{
		SignalMessage signalMsg = new SignalMessage(msg);
		if(signalMsg.getTypeMessage().equals(Key.ONE)){
			sendChatOne(signalMsg);
		}
		if(signalMsg.getTypeMessage().equals(Key.GROUP)){
			System.out.println("TIN NHAN CUA GROUP");
			sendChatGroup(signalMsg, msg);
		}
		if(signalMsg.getTypeMessage().equals(Key.ALL)){
			sendChatAll(msg);
		}
	}

	public void sendChatOne(SignalMessage signalMsg) throws IOException {
		for (int i = 0; i < vClient.size(); ++i) {
			if (signalMsg.getRecv().equals(vClient.get(i).account)) {
				String msg = "{ \"type\" : \"msg\", \"typeMsg\" : \"one\", \"sender\" : \""
						+ this.account
						+ "\", \"content\" : \""
						+ signalMsg.getContent() + "\" }";
				vClient.get(i).os.writeUTF(msg+"\r\n");
				break;
			}
		}
	}
	
	public void sendChatGroup(SignalMessage signalMsg,String msg) throws IOException {
		for (int j = 0; j < listGroup.size(); ++j) {
			if (listGroup.get(j).idGroup.equals(signalMsg.getIdGroup())) {
				sendMsgToAllAccountInGroup(listGroup.get(j), signalMsg,msg);
				break;
			}
		}
	}

	public void sendMsgToAllAccountInGroup(Group group,
			SignalMessage signalMsg, String msg) throws IOException {
		for (int i = 0; i < vClient.size(); ++i) {
			for (int j = 0; j < group.listAccount.size(); ++j) {
				if (vClient.get(i).account.equals(group.listAccount.get(j))
						&& !vClient.get(i).account
								.equals(signalMsg.getSender())) {
					vClient.get(i).os.writeUTF(msg + "\r\n");
				}
			}
		}
	}
	
	public void sendChatAll(String msg) throws IOException {
		for (int i = 0; i < vClient.size(); ++i) {
			if (!vClient.get(i).account.equals(this.account)
					&& listAccount.indexOf(vClient.get(i).account) >= 0) {
				vClient.get(i).os.writeUTF(msg + "\r\n");
			}
		}
	}
	
	public void sendResultLogin(String msg) throws IOException{
		Login login = new Login();
		SignalLogin signalLogin = new SignalLogin(msg);
		if (login.checkAccount(signalLogin.getAccount(), signalLogin.getPass())
				&& listAccount.indexOf(signalLogin.getAccount()) < 0) {
			account = signalLogin.getAccount();
			listAccount.add(account);
			SForm.listOnline.add(account);
			updateAddListOnline();
			msg = "{ \"type\" : \"log\", \"result\" : \"true\"}";
			System.out.println("LOGIN SUCCESS !");
			os.writeUTF(msg);
		} else {
			msg = "{ \"type\" : \"log\", \"result\" : \"flase\"}";
			os.writeUTF(msg + "\r\n");
		}
	}
	
	public void updateAddListOnline() throws IOException{
		for (int i = 0; i < vClient.size(); ++i) {
			for (int j = 0; j < listAccount.size(); ++j) {
				if (!vClient.get(i).account.equals(this.account)
						&& vClient.get(i).account.equals(listAccount.get(j))) {
					String msg = "{ \"type\":\"onl\",\"action\" : \"add\",\"acc\":\""
							+ this.account + "\" }";
					vClient.get(i).os.writeUTF(msg + "\r\n");
				}
			}
		}
		for (int i = 0; i < listAccount.size(); ++i) {
			if (!listAccount.get(i).equals(this.account)) {
				String msg = "{ \"type\":\"onl\",\"action\" : \"add\",\"acc\":\""
						+ listAccount.get(i) + "\" }";
				os.writeUTF(msg + "\r\n");
			}
		}
	}
	
	public void updateRemoveListOnline() throws IOException{
		vClient.remove(this);
		listAccount.remove(this.account);
		SForm.listOnline.remove(account);
		for (int i = 0; i < vClient.size(); ++i) {
			for (int j = 0; j < listAccount.size(); ++j) {
				if (vClient.get(i).account.equals(listAccount.get(j))) {
					String msg = "{ \"type\":\"onl\",\"action\" : \"remove\",\"acc\":\""
							+ this.account + "\" }";
					vClient.get(i).os.writeUTF(msg + "\r\n");
				}
			}
		}
	}
}
