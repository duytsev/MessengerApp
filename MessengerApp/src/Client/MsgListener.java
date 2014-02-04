package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JTextArea;

import Msg.Message;
import Msg.MessageUtils;
import date_helpers.dateHelper;

/*
 * A Thread class for the Client.
 * It handling incoming messages from the server and prints it to the TextArea in GUI.
 * I made this class implements Runnable, to avoid conflicts between sending and receiving messages,
 * because method readObject occupies a thread.
 * Also this class closes streams and socket in finally block.
 */

public class MsgListener implements Runnable {
	
	private Socket s;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private JTextArea ta;
	private Message msg;
	
	public MsgListener(Socket s, ObjectInputStream ois, ObjectOutputStream oos,JTextArea ta) { 
		this.s = s;
		this.ois = ois;
		this.oos = oos;
		this.ta = ta;
	}
	
	@Override
	public void run() {
		try {
			try {
				while ( (msg = (Message) ois.readObject()) != null ) {
					switch (msg.getType()) {
						case MessageUtils.MSG : ta.append(msg.getText() + "\n");
							break;
						case MessageUtils.CONNECTED : ta.append(msg.getText() + "\n");
													  Client_gui.updateUserList(msg.userList);
							break;
						case MessageUtils.DISCONNECTED : ta.append(msg.getText() + "\n");
													  Client_gui.updateUserList(msg.userList);
							break;
							
						case MessageUtils.ERROR : Client_gui.lockGUI();
												  Client_gui.nameIsTaken();
												  oos.writeObject(null);
							break;
					}
					
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
				try {
					ois.close();
					oos.close();
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
