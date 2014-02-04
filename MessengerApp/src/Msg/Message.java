package Msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/*
 * Message class implements Serializable to send
 * its objects via objects streams
 * Also it stores userList for GUI - every message contains an updated user list
 */

public class Message implements Serializable {
	private int type = 0;
	private String name = null;
	private String text = null;
	
	public ArrayList<String> userList = new ArrayList<String>();
	
	public Message(){};
	
	public Message(int type, String name){
		this.type = type;
		this.name = name;
		this.text = "";
	}
	
	public Message(int type, String name, String message) {
		this.type = type;
		this.name = name;
		this.text = text;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
}
