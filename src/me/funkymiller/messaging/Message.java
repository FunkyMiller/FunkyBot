package me.funkymiller.messaging;

import com.cavariux.twitchirc.Chat.User;

public class Message {
	private User msgTo;
	private String msgContent;
	private String msgType;
	
	public Message(String content) {
		msgTo = null;
		msgContent = content;
		msgType = "M";
	}
	
	public Message(String content, String type) {
		msgTo = null;
		msgContent = content;
		msgType = type.toUpperCase();
	}
	
	public Message(User user, String content, String type) {
		msgTo = user;
		msgContent = content;
		msgType = type.toUpperCase();
	}
	
	public User getUser() {
		return msgTo;
	}
	
	public String getContent() {
		return msgContent;
	}
	
	public String getType() {
		return msgType;
	}
}
