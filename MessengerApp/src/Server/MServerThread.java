package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Msg.Message;
import Msg.MessageUtils;
import date_helpers.dateHelper;

/*
 * A Thread class for the server, provides connection between
 * client and server
 * Creates i/o streams for user
 * Adds user to list
 * Handles user messaging
 * Finally closes streams and socket
 */

public class MServerThread implements Runnable {
	private int id = 0;
	private Socket socket = null;
	private String name = null;
	private Message msg = null;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	//Adds a new user to hash map
	private void addUser() {
		synchronized (MServer.users)  {
			if (!MServer.users.containsKey(name)) {
				MServer.users.put(name, oos);
			}
		}
	}
	
	//Sends a message log to User
	private void sendLog() throws IOException {
		for(Message msg : MServer.log.getRawList()) {
			oos.writeObject(msg);
		}
	}
	
	public MServerThread(int id, Socket socket) {
		this.id = id;
		this.socket = socket;
	}
	
	public void run() {    
		
		try {
			//Creating i/o streams
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			
			//Getting a first user message - new user connected
			try {
				msg = new Message();
				msg = (Message) ois.readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			MServer.log.addMessage(msg);
			name = msg.getName();
			
			////////////////////////////////////
			/*First, add a new user to hashmap 
			 *Then we are updating userList in Message object and send this info to others
			 */
			synchronized (MServer.users)  {
				if (!MServer.users.containsKey(name)) {
					
					System.out.println(msg.getText());
					
					MServer.users.put(name, oos);
					
					for(String name : MServer.users.keySet()) {
						msg.userList.add(name);
					}
					
					for (ObjectOutputStream writer : MServer.users.values()) {
						if(!writer.equals(oos)) {
							writer.writeObject(msg);
						}
					}
					
					sendLog();
				}
				else {
					//name is already taken
					Message err = new Message(MessageUtils.ERROR, name);
					oos.writeObject(err);
					oos.writeObject(null);
				}
			}
			////////////////////////////////////
			
			
			/*Here we are handling a messages from user
			 *If msg = null, then client disconnected
			 */
			try {
				while ( (msg = (Message) ois.readObject()) != null) {
					MServer.log.addMessage(msg);
					if (msg.getType() == Msg.MessageUtils.DISCONNECTED) { 	//if user disconnects
						oos.writeObject(null);
						synchronized (MServer.users) { 						//removing user from hashmap
							MServer.users.remove(msg.getName());
						}
						for(String name : MServer.users.keySet()) { 		//updating userlist
							msg.userList.add(name);
						}
						
						System.out.println(msg.getText());
					}				
					else 
						System.out.println(msg.getText());
					
					for (ObjectOutputStream writer : MServer.users.values()) { //and then send a message to others
						writer.writeObject(msg);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} catch ( IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
