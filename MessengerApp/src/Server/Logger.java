//Реальзация логгера сообщений с помощью циклической очереди
package Server;

import java.util.LinkedList;

import Msg.Message;

public class Logger {
	
	private int size;
	private LinkedList<Message> log = new LinkedList<Message>();
	
	public Logger(int size) {
		this.size = size;
	}
	
	public void addMessage(Message msg) {
		synchronized (log) {
			if (log.size() > size) {
				log.remove();
				log.add(msg);
			} else
				log.add(msg);
		}
	}
	
	public Message getMessage(int index) {
		return log.get(index);
	}
	
	public int getSize() {
		return this.size;
	}
	
	public LinkedList<Message> getRawList() {
		return this.log;
	}
}
