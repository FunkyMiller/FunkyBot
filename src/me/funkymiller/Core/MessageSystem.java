package me.funkymiller.Core;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;

import me.funkymiller.messaging.Message;
import me.funkymiller.messaging.SimpleCommand;
import me.funkymiller.highlighting.HighlightSystem;

public class MessageSystem {
	
	private Logger log;
	
	private String cmdPrefix;
	private HashMap<String,SimpleCommand> simpleComms;
	
	//Highlighting variables
	private HighlightSystem highlightSystem;
	private boolean highlightsOn;
	private String highlightsLevel;
	
	//twitch stuff
	private String oauthtoken;
	
	//cooldown hashmap
	private HashMap<String,Long> cooldown;
	private boolean sendCooldownWhisper = false;
	private boolean sendNoLevelWhisper = false;

	public MessageSystem(String prefix) {
		log = LoggerFactory.getLogger(getClass());
		cmdPrefix = prefix;
		cooldown = new HashMap<String,Long>();
	}
	
	public Message doCommand(Channel channel, String message, User user) {
		Message reply = null;
		String[] msgSplit = message.split(" ",2);
		String command = msgSplit[0].replaceFirst(cmdPrefix, "");
		String args = "";
		if (msgSplit.length > 1) {
			args = msgSplit[1];
		}
		
		//First check for simple reply commands
		if (simpleComms.containsKey(command)) {
			SimpleCommand thisComm = simpleComms.get(command);
			//Check level permissions
			if (!(canIRunCommand(channel, user, thisComm.getLevel()))) {
				if (sendNoLevelWhisper) {
					reply = new Message(user, "User level '" + thisComm.getLevel() + "' required to run command '" + command + "'", "W");
				}
				return reply;
			//Check cool downs
			} else {
				Long secsTilRun = timeUntilCooled(channel, user, thisComm);
				if (secsTilRun > 0) {
					if (sendCooldownWhisper){
						reply = new Message(user, "Cooldown on command '" + command + "' still in effect. Try again in " + secsTilRun + " seconds", "W");
					}
					return reply;
				} else {
					//send back the reply
					reply = new Message(user, thisComm.getReply(), "M");
					setCooldown(channel, user, thisComm);
					return reply;
				}
			}
		}
		
		switch (command) {
		//highlighting commands
		case "highlight":
		case "hl":
			//check if highlighting is on
			if (!(highlightsOn)) {
				log.info("User '" + user + "' tried to store a highlight, but the system is off ");
				reply = new Message(user, "Highlighting is not active", "W");
				return reply;
			}
			//check if they have the level to run it
			if (canIRunCommand(channel, user, highlightsLevel)) {
				highlightSystem.addHighlight(channel, user, args);
			} else {
				String msg = "";
				switch (highlightsLevel.toUpperCase()) {
				case "M":
					msg = "Highlighting is only available to Moderators";
					break;
				case "S":
					msg = "Highlighting is only available to Subscribers";
					break;
				case "F":
					msg = "Highlighting is only available to Followers";
				}
				reply = new Message(user, msg, "M");
				return reply;
			}
			break;
			
		//points commands
		
			
		//quotes commands
		case "addquote":
		case "quoteadd":
		case "addq":
			//add quote to proposed quotes list
			break;
		case "quote":
		case "qoute":
		case "kwote":
		case "q":
			//get a random quote and say it
			break;
		}
		
		return reply;
	}
	
	public void loadSimpleCommands() {
		
	}
	
	public void setHighlights(boolean hlStatus) {
		if (hlStatus) {
			highlightsOn = true;
			highlightsLevel= "mod";
			highlightSystem = new HighlightSystem();
		} else {
			highlightsOn = false;
			highlightsLevel = null;
			highlightSystem = null;
		}
	}
	
	public void setHighlights(String hlStatus) {
		if (hlStatus=="true") {
			highlightsOn = true;
			highlightsLevel= "mod";
			highlightSystem = new HighlightSystem();
		} else {
			highlightsOn = false;
			highlightsLevel = null;
			highlightSystem = null;
		}
	}
	
	public boolean isHighlightsOn() {
		return highlightsOn;
	}
	
	
	public void setHighlightsLevel(String lev) {
		switch (lev.toLowerCase()) {
		case "caster":
		case "mod":
		case "reg":
		case "all":
			highlightsLevel = lev.toLowerCase();
			break;
		default:
			highlightsLevel = "mod";
			break;
		}
	}
	
	public String getHighlightsLevel() {
		return highlightsLevel;
	}
	
	public boolean canIRunCommand(Channel channel, User user, String level) {
		switch (level.toUpperCase()) {
		case "S":
			if (!(channel == null || oauthtoken == null)) {
				return user.isSubscribed(channel, oauthtoken) || user.isMod(channel);
			} else {
				log.info("Channel and oauth token not set, so subscriber level defaulting to mod level via fall through");
			}
		case "M":
			return user.isMod(channel);
		case "F":
			return user.isFollowing(channel);
		default:
			//If we arent setting it to <M>od, <S>ubscriber or <F>ollower, that means anyone can do it!
			return true;
		}
	}
	
	public boolean canIRunCommand(Channel channel, User user, SimpleCommand comm) {
		String cooldownKey = getCooldownKey(channel, user, comm);
		Long lastCool = (long) 0;
		if (cooldown.containsKey(cooldownKey)) {
			lastCool = cooldown.get(cooldownKey);
		}
		Long timeSince = (System.currentTimeMillis() - lastCool) / 1000;
		switch (comm.getLevel().toUpperCase()) {
		case "S":
			if (channel == null || oauthtoken == null) {
				//fall through and run as if level is mod
			} else {
				if (user.isSubscribed(channel, oauthtoken) && timeSince >= comm.getSubCool()) {
					return true;
				} 
			}
		case "M":
			break;
		case "F":
			break;
		default:
			//Apparently anyone can do this :/
			break;
		}
		return false;
	}
	
	public void setCooldown(Channel channel, User user, SimpleCommand comm) {
		String key = user + "|<>|" + channel + "|<>|" + comm.getCommand();
		cooldown.put(key, System.currentTimeMillis());
	}
	
	public String getCooldownKey(Channel channel, User user, SimpleCommand comm) {
		return user + "|<>|" + channel + "|<>|" + comm.getCommand();
	}
	
	public String getCooldownKey(Channel channel, User user, String comm) {
		return user + "|<>|" + channel + "|<>|" + comm;
	}
	
	public Long timeUntilCooled(Channel channel, User user, SimpleCommand comm) {
		Long secs;
		String cooldownKey = getCooldownKey(channel, user, comm);
		Long lastCool = (long) 0;
		if (cooldown.containsKey(cooldownKey)) {
			lastCool = cooldown.get(cooldownKey);
		}
		secs = (System.currentTimeMillis() - lastCool) / 1000;
		
		return secs;
	}
	
	public void setOauthToken(String token) {
		oauthtoken = token;
	}
}
