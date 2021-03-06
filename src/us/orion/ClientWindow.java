package us.orion;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea txtrHistory = new JTextArea();
	private DefaultCaret caret;
	
	private Client client;
	private Thread listen, run;
	
	private boolean running = false;
		
	public ClientWindow(String name, String address, int port) {
		setTitle("ChatRoom Test");
		client = new Client(name, address, port);
		boolean connect = client.openConnection(address);
		if(!connect) {
			System.err.println("Connection failed!");
		}
		createWindow();
		console("Attempting a connection to " + address + ":"+ port + ", user: " + name);
		String connection = "/c/" + name; 
		client.send(connection.getBytes());
		running = true;
		run = new Thread(this, "Running");
		run.start();
	}

	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{28, 815, 30, 7};
		gbl_contentPane.rowHeights = new int[]{35,475, 40};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		txtrHistory.setEditable(false);
		caret = (DefaultCaret)txtrHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(txtrHistory);
		GridBagConstraints gbc_txtrHistory = new GridBagConstraints();
		gbc_txtrHistory.insets = new Insets(0, 0, 5, 5);
		gbc_txtrHistory.fill = GridBagConstraints.BOTH;
		gbc_txtrHistory.gridx = 0;
		gbc_txtrHistory.gridy = 0;
		gbc_txtrHistory.gridwidth = 3;
		gbc_txtrHistory.gridheight = 2;
		gbc_txtrHistory.insets = new Insets(0,5,0,0);
		contentPane.add(scroll, gbc_txtrHistory);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText(), true);
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText(), true);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect = "/d/"+ client.getID() + "/e/";
				send(disconnect, false);
				client.close();
				running = false;
			}
		});
		
		setVisible(true);
		txtMessage.requestFocusInWindow();
	}
	
	private void send(String message, boolean text) {
		if(message.equals(""))
			return;
		if(text) {
			message = client.getName() + ": " + message;
			message = "/m/" + message;
		}
		client.send(message.getBytes());
		txtMessage.setText("");
		txtMessage.requestFocusInWindow();
	}
	
	public void listen() {
		listen = new Thread() {
			public void run() {
				while(running) {
					String message = client.receive();
					if(message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Successfully connected to server! ID: " + client.getID());
					} else if(message.startsWith("/m/")) {
						String text = message.split("/m/|/e/")[1];
						console(text);
					}
				}
			}
		};
		listen.start();
	}
 	public void console(String message) {
		txtrHistory.append(message + "\n\r");
		txtrHistory.setCaretPosition(txtrHistory.getDocument().getLength());

	}
 	
 	public void run() {
 		listen();
 	}
}
