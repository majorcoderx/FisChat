package com.fis.client.form;

import java.awt.EventQueue;
import java.awt.List;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import com.fis.client.core.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class NewForm extends Thread{

	private JFrame frame;
	private String chooseAcc = "";
	private Vector<String> listGroup = new Vector<String>();
	private Vector<String> listChat = new Vector<String>();
	private JTextArea textListAccount;
	private JButton btnDone;
	private JButton btnAdd;
	private Client client;
	private String account;
	private int typeCreate;
	private JComboBox combobox;
	
	public NewForm(Client client,String account) {
		initialize();
		frame.setVisible(true);
		this.client = client;
		this.account = account;
		typeCreate = 0;
	}
	
	public NewForm(String recv,Client client,String account) {
		initialize();
		frame.setVisible(true);
		listChat.add(recv);
		textListAccount.append("|" + recv + "| ");
		typeCreate = 1;
		this.account = account;
		this.client = client;
	}

	private void initialize() {
		frame = new JFrame("Add your friends");
		frame.setResizable(false);
		frame.setBounds(100, 100, 378, 159);
		frame.getContentPane().setLayout(null);
		
		for(int i = 0 ; i < CForm.listOnl.countItems(); ++i){
			listGroup.add(CForm.listOnl.getItem(i));
		}
		
		chooseAcc = listGroup.firstElement();
		
		btnAdd = new JButton("+");
		btnAdd.setBounds(236, 10, 47, 23);
		frame.getContentPane().add(btnAdd);
		
		btnDone = new JButton("Done");
		btnDone.setBounds(293, 10, 59, 23);
		frame.getContentPane().add(btnDone);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 39, 342, 73);
		frame.getContentPane().add(scrollPane);
		
		textListAccount = new JTextArea();
		textListAccount.setEditable(false);
		scrollPane.setViewportView(textListAccount);
		
		combobox = new JComboBox(listGroup);
		combobox.setBounds(12, 11, 214, 21);
		frame.getContentPane().add(combobox);
		combobox.setSelectedIndex(0);
		combobox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox jcbType = (JComboBox) e.getSource();
				chooseAcc = (String) jcbType.getSelectedItem();
			}
		});
		
		btnDone.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(listChat.size() == 0){
					JOptionPane.showMessageDialog(frame, "No choose !");
					return;
				}
				if(listChat.size() > 1){
					System.out.println("List Chat: "+listChat);
					System.out.println("Account: " + account);
					if(client != null){
						System.out.println("CLIENT");
					}
					GForm gForm = new GForm(client, listChat, account);
					CForm.listChatGroup.add(gForm);
					frame.setVisible(false);
				}
				else{
					if(typeCreate == 0){
						ChForm chFrom = new ChForm(listChat.firstElement(), client, account);
						CForm.listChatForm.add(chFrom);
						frame.setVisible(false);
					}
					else{
						JOptionPane.showMessageDialog(frame, "Don't create, this created !");
						return;
					}
				}
			}
		});
		
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// add item
				if (listChat.indexOf(chooseAcc) < 0) {
					listChat.add(chooseAcc);
					textListAccount.append("|" + chooseAcc + "| ");
				} else {
					JOptionPane.showMessageDialog(frame, "Item selected !");
					return;
				}
			}
		});
	}
}
