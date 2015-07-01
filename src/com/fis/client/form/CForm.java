package com.fis.client.form;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.List;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JScrollPane;

import com.fis.client.core.Client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPasswordField;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowFocusListener;

public class CForm extends Thread{

	private JFrame frame;
	private JTextField textAcc;
	private JTextField textHost;
	private JTextField textPort;
	private JTextField textMsg;
	private JButton btnLogin;
	private JButton btnSend;
	private JButton btnLogout;
	private Client client;
	private JPasswordField textPass;
	
	
	public static List listOnl;
	public static List listGroup;
	public static JTextArea textChat;
	
	public static ArrayList<ChForm> listChatForm = new ArrayList<ChForm>();
	public static ArrayList<GForm> listChatGroup = new ArrayList<GForm>();
	public static Vector<String> listOnlGr = new Vector<String>();
	public static boolean login = false;
	private JButton btnNewMsg;

	public static void main(String[] args) {
		CForm clientForm = new CForm();
		clientForm.start();
	}
	
	public void run(){
		while(true){
			btnLogout.setEnabled(login);
			btnSend.setEnabled(login);
			btnLogin.setEnabled(!login);
			textAcc.setEnabled(!login);
			textPass.setEnabled(!login);
			textHost.setEnabled(!login);
			textPort.setEnabled(!login);
			textMsg.setEnabled(login);
			if(listOnl.countItems() > 0 && login){
				btnNewMsg.setEnabled(login);
			}
			else{
				btnNewMsg.setEnabled(false);
			}
		}
	}

	public CForm() {
		initialize();
		textPort.setText("111");
		textPass.setText("111");
		frame.setVisible(true);
	}

	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int keep = JOptionPane.showConfirmDialog(null,
						"Would you like close application?", "Shutdown",
						JOptionPane.YES_NO_OPTION);
				
				if (keep == 0 && listChatForm.isEmpty() && listChatGroup.isEmpty()) {
					if (login) {
						client.logoutSystem();
					}
					System.exit(1);
				}
				if(!listChatForm.isEmpty()){ 
					for(int i = 0 ;i < listChatForm.size(); ++i){
						client.sendMsgOne("out", listChatForm.get(i).recv, textAcc.getText());
					}
					listChatForm.clear();
					System.exit(1);
				}
				if(!listChatGroup.isEmpty()){
					for(int i = 0; i < listChatGroup.size(); ++i){
						if(textAcc.getText().equals(listChatGroup.get(i).admin)){
							client.turnOfGroup(listChatGroup.get(i).idGroup);
							listChatGroup.get(i).frame.setVisible(false);
						}
						else{
							client.removeAccountGroup(listChatGroup.get(i).idGroup, textAcc.getText());
							listChatGroup.get(i).frame.setVisible(false);
						}
					}
					listChatGroup.clear();
					System.exit(1);
				}
			}
		});
		frame.setBounds(100, 100, 552, 398);
		frame.getContentPane().setLayout(null);
		
		JLabel lblAccount = new JLabel("Account");
		lblAccount.setBounds(10, 46, 46, 14);
		frame.getContentPane().add(lblAccount);
		
		textAcc = new JTextField();
		textAcc.setBounds(70, 43, 143, 20);
		frame.getContentPane().add(textAcc);
		textAcc.setColumns(10);
		
		JLabel lblPass = new JLabel("Pass");
		lblPass.setBounds(10, 71, 46, 14);
		frame.getContentPane().add(lblPass);
		
		btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(login) return;
				if(textHost.getText().equals("") || textPort.getText().equals("") 
						|| textAcc.getText().equals("") || textPass.getText().equals("") ){
					JOptionPane.showMessageDialog(frame, "Input empty at some record !");
				}
				else{
					try{
						int port = Integer.parseInt(textPort.getText());
						client = new Client(textHost.getText(), port);
						client.start();
						client.sendLogin(textAcc.getText(), textPass.getText());
					}catch(NumberFormatException ex){
						ex.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Input text is't number !");
					}
				}
			}
		});
		btnLogin.setBounds(132, 99, 72, 23);
		frame.getContentPane().add(btnLogin);
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane.setBounds(10, 133, 276, 181);
		frame.getContentPane().add(scrollPane);
		
		textChat = new JTextArea();
		scrollPane.setViewportView(textChat);
		
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(10, 11, 46, 14);
		frame.getContentPane().add(lblAddress);
		
		textHost = new JTextField("localhost");
		textHost.setBounds(68, 8, 218, 20);
		frame.getContentPane().add(textHost);
		textHost.setColumns(10);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(296, 11, 46, 14);
		frame.getContentPane().add(lblPort);
		
		textPort = new JTextField();
		textPort.setBounds(327, 8, 86, 20);
		frame.getContentPane().add(textPort);
		textPort.setColumns(10);
		
		btnLogout = new JButton("Logout");
		btnLogout.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!login) return;
				client.logoutSystem();
				login = false;
				resetText();
			}
		});
		btnLogout.setBounds(214, 99, 72, 23);
		frame.getContentPane().add(btnLogout);
		
		listOnl = new List();
		listOnl.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!login)
					return;
				String recv = listOnl.getItem(((int) e.getItem()));
				boolean checkOpenChForm = true;
				for(int i = 0; i < listChatForm.size(); ++i){
					if(listChatForm.get(i).recv.equals(recv)){
						checkOpenChForm = false;
					}
				}
				if(checkOpenChForm){
					ChForm chForm = new ChForm(recv, client, textAcc.getText());
					listChatForm.add(chForm);
				}
				else{
					JOptionPane.showMessageDialog(frame, "Windows opened !");
				}
			}
		});
		listOnl.setBounds(296, 46, 116, 268);
		frame.getContentPane().add(listOnl);
		
		listGroup = new List();
		listGroup.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//group list
				if (!login)
					return;
				String idGroup = listGroup.getItem(((int) e.getItem()));
				for(int i = 0 ; i < listChatGroup.size(); ++i){
					if(listChatGroup.get(i).idGroup.equals(idGroup)){
						if(listChatGroup.get(i).frame.isVisible()){
							return;
						}
						else{
							listChatGroup.get(i).frame.setVisible(true);
						}
					}
				}
			}
		});
		listGroup.setBounds(418, 46, 122, 268);
		frame.getContentPane().add(listGroup);
		
		textMsg = new JTextField();
		textMsg.setBounds(10, 325, 276, 27);
		frame.getContentPane().add(textMsg);
		textMsg.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!login) return;
				if(!textMsg.getText().equals("")){
					client.sendMsgAll(textMsg.getText(), textAcc.getText());
					textChat.append("<<Me>>" + textMsg.getText()+"\n");
					textMsg.setText("");
				}
			}
		});
		btnSend.setBounds(296, 327, 72, 23);
		frame.getContentPane().add(btnSend);
		
		textPass = new JPasswordField();
		textPass.setBounds(70, 68, 143, 20);
		frame.getContentPane().add(textPass);
		
		btnNewMsg = new JButton("+");
		btnNewMsg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!login || listOnl.countItems() == 0) return;
				NewForm newForm = new NewForm(client,textAcc.getText());
			}
		});
		btnNewMsg.setBounds(490, 327, 46, 23);
		frame.getContentPane().add(btnNewMsg);
	}
	
	public void resetText(){
		textHost.setText("");
		textPort.setText("");
		textChat.setText("");
		textAcc.setText("");
		textPass.setText("");
		listOnl.removeAll();
	}
}
