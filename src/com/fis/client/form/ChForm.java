package com.fis.client.form;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.fis.client.core.Client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChForm {

	public JFrame frame;
	private JTextField textMsg;
	private Client client;
	public String account;
	public String recv;
	
	public static JTextArea textChat;

	public ChForm(String recv, Client client, String sender) {
		this.client = client;
		this.account = sender;
		this.recv = recv;
		frame = new JFrame(sender + " >> " + recv);
		frame.setResizable(false);
		
		frame.setBounds(100, 100, 382, 318);
		frame.getContentPane().setLayout(null);
		initialize();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	private void initialize() {
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 43, 346, 190);
		frame.getContentPane().add(scrollPane);
		
		textChat = new JTextArea();
		scrollPane.setViewportView(textChat);
		
		textMsg = new JTextField();
		textMsg.setBounds(10, 238, 270, 30);
		frame.getContentPane().add(textMsg);
		textMsg.setColumns(10);
		
		JButton btnSend = new JButton(">>>");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!textMsg.equals("")) {
					client.sendMsgOne(textMsg.getText(), recv, account);
					textChat.append("<Me>" + textMsg.getText()+"\n");
					textMsg.setText("");
				}
			}
		});
		btnSend.setBounds(291, 244, 65, 23);
		frame.getContentPane().add(btnSend);
		
		JButton btnCreate = new JButton("+");
		btnCreate.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				NewForm newForm = new NewForm(recv,client,account);
			}
		});
		btnCreate.setBounds(315, 11, 41, 23);
		frame.getContentPane().add(btnCreate);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				int keep = JOptionPane.showConfirmDialog(null,
						"Would you like go out chat session?", "Chat Session",
						JOptionPane.YES_NO_OPTION);
				if (keep == 0){
					client.sendMsgOne("out", recv, account);
					for(int i = 0 ; i < CForm.listChatForm.size(); ++i){
						if(CForm.listChatForm.get(i).recv.equals(recv)){
							CForm.listChatForm.remove(i);
						}
					}
					frame.setVisible(false);
				}
			}
		});
	}
}
