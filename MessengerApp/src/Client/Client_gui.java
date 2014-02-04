package Client;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/*
 * Client Swing GUI.
 * It uses GridBagLayout as a Layout manager.
 */

public class Client_gui extends JFrame implements KeyListener {
	
	private static JTextArea taInput;
	private static JTextField textField;
	private static JButton btnConnect;
	private static JButton btnDisconnect;
	private static JButton btnSend;
	private static MClient client = null;
	
	private static DefaultListModel listModel = new DefaultListModel();
	
	public static JTextArea taOutput;
	
	public JList lstUserList;
	
	/*
	 * Simple name checking using regular expressions.
	 */
	private boolean checkName(String name) {
		
		Pattern pt = Pattern.compile( "^[a-zA-Z][\\w]{2,9}$" );
		Matcher m = pt.matcher(name);
		return m.matches();
	}
	
	private void incorrectNameDW() {
		JOptionPane.showMessageDialog(null, "Incorrect name!");
	}
	
	/*
	 * Method that takes name and ip-adress via dialog window textfields,
	 * checks them and start the client.
	 */
	private void showAuthBox() throws IOException {
		JTextField tfName = new JTextField("<name>");
		JTextField tfIPAdress = new JTextField("127.0.0.1");
		
		
		Object[] msg = {"IP Adress", tfIPAdress, 
						"Name", tfName};
		
		Object[] options = {"Yes", "No"};
		
		JOptionPane pane = new JOptionPane(msg, 
									JOptionPane.PLAIN_MESSAGE, 
									JOptionPane.OK_CANCEL_OPTION);
		
		int option = JOptionPane.showOptionDialog(null, msg, "Auth", 
											JOptionPane.OK_CANCEL_OPTION, 
											JOptionPane.QUESTION_MESSAGE, 
											null, options, options[0]);
		if (option == JOptionPane.OK_OPTION) {
			try {
				if(checkName(tfName.getText())) {
					client = new MClient(tfName.getText(), tfIPAdress.getText());
					client.Start();
					setTitle("Chat App: " + tfName.getText());
					taOutput.setText("");
					btnDisconnect.setEnabled(true);
					btnConnect.setEnabled(false);
					taInput.setEditable(true);
					btnSend.setEnabled(true);
					client.listenMsg(taOutput);
				}
				else incorrectNameDW();
			} catch (ConnectException e) {
				client = null;
				serverIsDown();
			}
		}
	}
	
	
	public Client_gui() {
		super("Text Area Test");
		setResizable(false);
		setBounds(400, 100, 900, 600);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{0, 332, 0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0};
		gridBagLayout.columnWidths = new int[] {215, 182, 0};
		getContentPane().setLayout(gridBagLayout);
		
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					taInput.setText("");
					showAuthBox();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.insets = new Insets(5, 5, 5, 5);
		gbc_btnConnect.anchor = GridBagConstraints.LINE_END;
		gbc_btnConnect.gridx = 0;
		gbc_btnConnect.gridy = 0;
		getContentPane().add(btnConnect, gbc_btnConnect);
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() { //TODO disconnect
			public void actionPerformed(ActionEvent event) {
				client.disconnect();
				lockGUI();
				taOutput.append("You have been disconnected from the server" + "\n");
				setTitle("Chat App");
			}
		});
		
		GridBagConstraints gbc_btnDisconnect = new GridBagConstraints();
		gbc_btnDisconnect.insets = new Insets(5, 5, 5, 5);
		gbc_btnDisconnect.anchor = GridBagConstraints.LINE_START;
		gbc_btnDisconnect.gridx = 1;
		gbc_btnDisconnect.gridy = 0;
		btnDisconnect.setEnabled(false);
		getContentPane().add(btnDisconnect, gbc_btnDisconnect);
		
		taOutput =  new JTextArea();
		taOutput.setEditable(false);
		taOutput.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		taOutput.setLineWrap(true);
		GridBagConstraints gbc_taOutput = new GridBagConstraints();
		gbc_taOutput.weightx = 4.0;
		gbc_taOutput.insets = new Insets(10, 10, 5, 10);
		gbc_taOutput.fill = GridBagConstraints.BOTH;
		gbc_taOutput.gridx = 0;
		gbc_taOutput.gridy = 1;
		gbc_taOutput.weighty = 1;
		
		JScrollPane scroll = new JScrollPane(taOutput); 
		getContentPane().add(scroll, gbc_taOutput);
				
		
		listModel = new DefaultListModel();
		lstUserList = new JList(listModel);
		lstUserList.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_taUserList = new GridBagConstraints();
		gbc_taUserList.weightx = 3.0;
		gbc_taUserList.gridwidth = 2;
		gbc_taUserList.insets = new Insets(10, 0, 5, 10);
		gbc_taUserList.fill = GridBagConstraints.BOTH;
		gbc_taUserList.gridx = 1;
		gbc_taUserList.gridy = 1;
		gbc_taUserList.weighty = 0;
		getContentPane().add(lstUserList, gbc_taUserList);
		
		taInput = new JTextArea("To write messages hit connect button");
		taInput.setEditable(false);
		taInput.addKeyListener(this);
		
		taInput.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		taInput.setLineWrap(true);
		GridBagConstraints gbc_taInput = new GridBagConstraints();
		gbc_taInput.weighty = 1.0;
		gbc_taInput.insets = new Insets(5, 10, 10, 10);
		gbc_taInput.fill = GridBagConstraints.BOTH;
		gbc_taInput.gridx = 0;
		gbc_taInput.gridy = 2;
		getContentPane().add(taInput, gbc_taInput);
		
		btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridwidth = 2;
		gbc_btnSend.insets = new Insets(5, 0, 5, 10);
		gbc_btnSend.fill = GridBagConstraints.BOTH;
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 2;
		getContentPane().add(btnSend, gbc_btnSend);
		
		btnSend.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!taInput.getText().equals("")) 
					sendMessage();	
			}
		});
		

	}
	
	/*
	 * Sends a message and clears TextArea
	 */
	private void sendMessage() {
		client.sendMsg(taInput.getText());
		taInput.setText("");
	}
	
	private void serverIsDown() {
		JOptionPane.showMessageDialog(null, "Server is down. Try to connect later.");
	}
	
	public static void updateUserList(ArrayList<String> userList){
		listModel.clear();
		for(String name : userList) listModel.addElement(name);
	}
	
	public static void nameIsTaken() {
		JOptionPane.showMessageDialog(null, "This name is already in use!");
	}
	
	
	public static void lockGUI() {
		taInput.setEditable(false);
		btnSend.setEnabled(false);
		btnDisconnect.setEnabled(false);
		btnConnect.setEnabled(true);
		listModel.clear();
		client = null;
	}
	
	public static void main(String[] args) {
		Client_gui app = new Client_gui();
		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (client != null) client.disconnect();
				e.getWindow().dispose();
			}
		});
		app.setVisible(true);
		app.setTitle("Chat App");
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ( !taInput.getText().equals("") && e.getKeyCode() == e.VK_ENTER) {
			sendMessage();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == e.VK_ENTER) e.consume();
		if (e.isShiftDown() && e.getKeyCode() == e.VK_ENTER) taInput.setText("\n"); //TODO multiline input
	}
}
