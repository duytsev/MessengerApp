package Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Main class for the server
 * Listen for a connection and accepts it.
 * When a new Client connects, Server creates a new thread 
 */

public class MServer {
	
	private static final int PORT = 7777;
	private static ServerSocket ss;
	private static int id = 0;
	
	/*
	 * Users hash map
	 * key - name
	 * value - output stream
	 */	
	public static Map<String, ObjectOutputStream> users = new HashMap<String, ObjectOutputStream>();
	public static Logger log = new Logger(10);
	
	public static void main(String[] args) throws IOException {
		boolean listening = true;
		
		ss = new ServerSocket(PORT);
		
		System.out.println("Waiting for connections...");
		
		try {
			while (listening) {
				new Thread(new MServerThread( id++, ss.accept() )).start();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally { ss.close(); }
	}
}
