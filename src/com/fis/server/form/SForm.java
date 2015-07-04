package com.fis.server.form;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.List;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import com.fis.server.core.Server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SForm extends Thread{

	private JFrame frame;
	private JTextField textPort;
	private JButton btnCloseServer;
	private JButton btnOpenServer;
	private JScrollPane scrollPane;
	private JTextField textMsg;
	private JButton btnSend;
	private Server server;
	
	public static JTextArea textAreaChat;
	public static List listGroup;
	public static List listOnline;
	public static boolean openServer = false;

	public static void main(String[] args) {
		SForm serverForm = new SForm();
		serverForm.start();
	}
	
	public void run(){
		while(true){
			System.out.print("");
			btnOpenServer.setEnabled(!openServer);
			btnCloseServer.setEnabled(openServer);
			btnSend.setEnabled(openServer);
			textMsg.setEnabled(openServer);
			textPort.setEnabled(!openServer);
			textAreaChat.setEnabled(openServer);
			listGroup.setEnabled(openServer);
			listOnline.setEnabled(openServer);
		}
	}

	public SForm() {
		initialize();
		frame.setVisible(true);
	}

	private void initialize() {
		frame = new JFrame("FIS SERVER");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg) {
				int keep = JOptionPane.showConfirmDialog(
					    null,
					    "Would you like close application?",
					    "Shutdown",
					    JOptionPane.YES_NO_OPTION);
				if(keep == 0){
					System.exit(1);
				}
			}
		});
		frame.setBounds(100, 100, 560, 324);
		frame.getContentPane().setLayout(null);
		
		textPort = new JTextField();
		textPort.setBounds(48, 7, 79, 20);
		frame.getContentPane().add(textPort);
		textPort.setColumns(10);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(10, 10, 46, 14);
		frame.getContentPane().add(lblPort);
		
		listGroup = new List();
		listGroup.setBounds(426, 10, 110, 218);
		frame.getContentPane().add(listGroup);
		
		listOnline = new List();
		listOnline.setBounds(310, 10, 110, 218);
		frame.getContentPane().add(listOnline);
		
		btnOpenServer = new JButton("Open ");
		btnOpenServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg) {
				if(openServer) return;
				try{
					int port = Integer.parseInt(textPort.getText());
					server = new Server(port);
					server.start();
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		});
		btnOpenServer.setBounds(137, 6, 71, 23);
		frame.getContentPane().add(btnOpenServer);
		
		btnCloseServer = new JButton("Close ");
		btnCloseServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(!openServer) return;
				server.closeServer();
				openServer = false;
				textAreaChat.setText("");
			}
		});
		btnCloseServer.setBounds(220, 6, 75, 23);
		frame.getContentPane().add(btnCloseServer);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 38, 283, 190);
		frame.getContentPane().add(scrollPane);
		
		textAreaChat = new JTextArea();
		scrollPane.setViewportView(textAreaChat);
		
		textMsg = new JTextField();
		textMsg.setBounds(10, 248, 214, 29);
		frame.getContentPane().add(textMsg);
		textMsg.setColumns(10);
		
		btnSend = new JButton(">>>");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!openServer) return;
				if(!textMsg.equals("")){
					textAreaChat.append("<SERVER>" + textMsg.getText() + "\n" );
					server.notifyAllUser(textMsg.getText());
					textMsg.setText("");
				}
			}
		});
		btnSend.setBounds(234, 251, 60, 23);
		frame.getContentPane().add(btnSend);
	}
}
