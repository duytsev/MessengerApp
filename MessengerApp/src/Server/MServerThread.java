package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Msg.Message;
import Msg.MessageUtils;
import date_helpers.dateHelper;

public class MServerThread implements Runnable {
	private int id = 0;
	private Socket socket = null;
	private String name = null;
	private Message msg = null;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private void addUser() {
		synchronized (MServer.users)  {
			if (!MServer.users.containsKey(name)) {
				MServer.users.put(name, oos);
			}

		}
	}
	
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
			
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		
			try {
				msg = new Message();
				msg = (Message) ois.readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			MServer.log.addMessage(msg);
			
			name = msg.getName();
			
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
					Message err = new Message(MessageUtils.ERROR, name);
					oos.writeObject(err);
					oos.writeObject(null);
				}
			}
			
//			for(String name : MServer.users.keySet()) {
//				msg.userList.add(name);
//			}
//			
//			for (ObjectOutputStream writer : MServer.users.values()) {
//				writer.writeObject(msg);
//			}
		
			try {
				while ( (msg = (Message) ois.readObject()) != null) {
					MServer.log.addMessage(msg);
					if (msg.getType() == Msg.MessageUtils.DISCONNECTED) {
						oos.writeObject(null);
						synchronized (MServer.users) {
							MServer.users.remove(msg.getName());
						}
						for(String name : MServer.users.keySet()) {
							msg.userList.add(name);
						}
						
						System.out.println(msg.getText());
					}				
					else 
						System.out.println(msg.getText());
					
					for (ObjectOutputStream writer : MServer.users.values()) {
						writer.writeObject(msg);
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch ( IOException e) {
			// TODO Auto-generated catch block
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
