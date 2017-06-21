package me.funkymiller.Core;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;
import com.cavariux.twitchirc.Core.TwitchBot;

import me.funkymiller.highlighting.HighlightSystem;
import me.funkymiller.messaging.Message;
import me.funkymiller.points.PointsSystem;


public class FunkyBot extends TwitchBot {
	
	//Log
	private Logger log;

	//Config variables
	private static Properties loadedConfig;
	private static ConfigSystem configSystem;
	
	//Twitch Connection Variables
	private static String botName;
	private static String botOauth;
	private static String botClientID;
	
	//Twitch Server and Channel
	private static String botChannel;
	
	//Sub systems
	private MessageSystem messageSys;
	private HighlightSystem highSys;
	private PointsSystem pointsSys;
	
	//Command message indicator
	private static String cmdIndicator;
	
	//Points System
	private static PointsSystem pointsSystem;
	
	public FunkyBot() {
		
		//Create log
		log = LoggerFactory.getLogger(getClass());
		log.info("Creating bot");
		
		//Create config system
		log.info("Loading configuration from config.properties");
		configSystem = new ConfigSystem("config.properties");
		loadedConfig = configSystem.getProperties();
		
		//Set the connection variables from the config file
		botName = loadedConfig.getProperty("bot_name");
		botOauth = loadedConfig.getProperty("bot_oauth");
		botClientID = loadedConfig.getProperty("bot_clientid");
		
		//Set the twitch server and channel
		botChannel = loadedConfig.getProperty("autojoin_channel");
		//prefix channel with # if they didnt include it
		if (!(botChannel.startsWith("#"))) {
			botChannel = "#" + botChannel;
		}
		
		//get the command message indicator
		cmdIndicator = loadedConfig.getProperty("command_message_prefix");
		if (cmdIndicator == null || cmdIndicator.length() == 0) {
			this.setCommandTrigger("!");
		} else {
			this.setCommandTrigger(cmdIndicator);
		}
		
		//Create the Sub Systems
		messageSys = new MessageSystem(this);
		highSys = new HighlightSystem(this);
		pointsSys = new PointsSystem(this);
		
		//Config the Message System
		
		//Config the highlight system
		highSys.setHighlights(loadedConfig.getProperty("highlights_enabled"));
		highSys.setHighlightsLevel(loadedConfig.getProperty("highlights_level"));
		
		//Config the Points System
		pointsSys.setPointsOn(loadedConfig.getProperty("points_enabled").equalsIgnoreCase("true"));
		
		//pass the settings in to the bot
		this.setUsername(botName);
		this.setOauth_Key(botOauth);
		this.setClientID(botClientID);
		
	}
	
	public static void main(String[] args) throws Exception {
		Logger log = LoggerFactory.getLogger("main");
		
		log.info("Starting Program");
		
		//Create bot object
		FunkyBot bot = new FunkyBot();
		log.info("Bot created");
		
		//connect to twitch
		bot.connect();
		
		//join channel
		bot.joinChannel(botChannel);
		
		//Finally start the bot, so it starts doing its job!
		log.info("Bot is now starting, order will be restored in 3...2...1");
		bot.start();
		
	}
	
	@Override
	public void onMessage(User user, Channel channel, String message) {
		//This is a simple message, not a command. Give them points if enabled.
		if ((!(pointsSystem == null)) && channel.isLive()) {
			pointsSystem.givePoints(channel, user, "message");
		}
	}
	
	@Override
	public void onCommand(User user, Channel channel, String message) {
		Message msgReply = null;
		log.debug("Processing command '" + message + "' from user '" + user + "' who " + (channel.isMod(user) ? " is " : " isnt") + " mod" );
		
		//run the message through the Message System
		msgReply = messageSys.doCommand(channel, message, user);
		
		//Send the reply message or whisper
		if (msgReply.getType().equalsIgnoreCase("W") && msgReply.getContent().length() > 0) {
			this.whisper(user,  msgReply.getContent());
			log.debug("sent whisper to '" + user + "' saying '" + msgReply.getContent() + "'");
			return;
		}
		if (msgReply.getType().equalsIgnoreCase("M") && msgReply.getContent().length() > 0) {
			this.sendMessage(msgReply.getContent(), channel);
			log.debug("sent message '" + msgReply.getContent() + "' to channel '" + channel + "'");
			return;
		}
	}
	
	@Override
	public void userJoins(User user, Channel channel) {
		
	}

	
	public MessageSystem getMessageSystem() {
		return messageSys;
	}
	
	public HighlightSystem getHighlightSystem() {
		return highSys;
	}
	
	public PointsSystem getPointsSystem() {
		return pointsSys;
	}
	
	public String getOauth() {
		return botOauth;
	}
	
}
