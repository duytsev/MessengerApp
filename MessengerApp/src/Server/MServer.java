package Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MServer {
	
	private static final int PORT = 7777;
	private static ServerSocket ss;
	private static int id = 0;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { ss.close(); }
	}

}
