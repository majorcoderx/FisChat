package com.fis.client.form;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.Vector;

import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.fis.client.core.Client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GForm{

	public JFrame frame;
	
	private JTextField textMsg;
	public Vector<String> listRecv;
	public JComboBox<String> cbblistGroup;
	public JTextArea textChat;
	public JButton btnDel;
	public JButton btnName;
	private boolean checkOpenBtn = true;
	private int indexSelected = 0;
	
	public String idGroup = "";
	private Client client;
	private String account;
	public String admin;
	private JButton btnOut;
	
	public GForm(Client client,Vector<String> listRecv, String acc) {
		idGroup+=acc;
		Random rand = new Random();
		int n = rand.nextInt(1000)+ 500;
		idGroup+=n;
		listRecv.add(acc);
		this.listRecv = listRecv;
		this.client = client;
		client.sendCreateGroup(idGroup, listRecv, acc);
		CForm.listGroup.add(idGroup);
		this.account = this.admin = acc; 
		initialize();
		frame.setVisible(true);
	}
	
	public GForm(Client client,Vector<String> listRecv, String acc, String admin,String idGroup) {
		this.listRecv = listRecv;
		this.client = client;
		this.account = acc;
		this.admin = admin;
		this.idGroup = idGroup;
		initialize();
		frame.setVisible(true);
		if(!acc.equals(admin)){
			btnDel.setEnabled(false);
			btnName.setEnabled(false);
			checkOpenBtn = false;
		}
	}

	private void initialize() {
		frame = new JFrame("Name: " + account);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				frame.setVisible(false);
			}
		});
		frame.setBounds(100, 100, 411, 312);
		frame.getContentPane().setLayout(null);
		btnDel = new JButton("-");
		btnDel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (listRecv.size() < 2 || !checkOpenBtn)
					return;
				if(listRecv.get(indexSelected).equals(account)){
					JOptionPane.showMessageDialog(frame, "Your don't delete you !");
					return;
				}
				client.removeAccountGroup(idGroup, listRecv.get(indexSelected));
//				listRecv.remove(indexSelected);
//				cbblistGroup.setSelectedIndex(0);
			}
		});
		btnDel.setBounds(185, 11, 53, 23);
		frame.getContentPane().add(btnDel);
		JScrollPane scrollPaneChat = new JScrollPane();
		scrollPaneChat.setBounds(10, 44, 375, 186);
		frame.getContentPane().add(scrollPaneChat);
		textChat = new JTextArea();
		scrollPaneChat.setViewportView(textChat);
		btnName = new JButton("+Name");
		btnName.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!checkOpenBtn) return;
				Object[] possibilities = CForm.listOnlGr.toArray();
				String name = (String) JOptionPane.showInputDialog(frame, "",
						"Add new friends", JOptionPane.PLAIN_MESSAGE, null,
						possibilities, "");
				if ((name != null) && (listRecv.indexOf(name) < 0)) {
//				    listRecv.add(0, name);
				    client.addAccountGroup(idGroup, name);
//				    cbblistGroup.setSelectedIndex(0);
				}
				else{
					JOptionPane.showMessageDialog(frame, "Can't add new friends !");
				}
			}
		});
		btnName.setBounds(242, 11, 67, 23);
		frame.getContentPane().add(btnName);
		textMsg = new JTextField();
		textMsg.setBounds(10, 237, 311, 29);
		frame.getContentPane().add(textMsg);
		textMsg.setColumns(10);
		JButton btnSend = new JButton(">>>");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!textMsg.getText().equals("")){
					client.sendMsgGroup(textMsg.getText(), account, idGroup);
					textChat.append("<Me> : " + textMsg.getText() + "\n");
					textMsg.setText("");
				}
			}
		});
		btnSend.setBounds(328, 240, 57, 23);
		frame.getContentPane().add(btnSend);
		cbblistGroup = new JComboBox<String>(listRecv);
		cbblistGroup.setBounds(12, 11, 163, 23);
		frame.getContentPane().add(cbblistGroup);
		cbblistGroup.setSelectedIndex(0);
		
		btnOut = new JButton("Out");
		btnOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(checkOpenBtn){
					System.out.println("ADMIN DONG GROUP");
					int keep = JOptionPane.showConfirmDialog(null,
							"Would you like close group chat?", "Close",
							JOptionPane.YES_NO_OPTION);
					if (keep == 0) {
						client.turnOfGroup(idGroup);
						CForm.listGroup.remove(idGroup);
						frame.setVisible(false);
					}
					else return;
				}
				else{
					int keep = JOptionPane.showConfirmDialog(null,
							"Would you like out group chat?", "Out",
							JOptionPane.YES_NO_OPTION);
					if (keep == 0) {
						client.removeAccountGroup(idGroup, account);
					}
					else return;
				}
			}
		});
		btnOut.setBounds(319, 11, 66, 23);
		frame.getContentPane().add(btnOut);
		cbblistGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox jcbType = (JComboBox) e.getSource();
				indexSelected = jcbType.getSelectedIndex();
			}
		});
	}
}
