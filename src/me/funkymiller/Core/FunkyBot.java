package me.funkymiller.Core;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;
import com.cavariux.twitchirc.Core.TwitchBot;

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
	
	//Command message indicator
	private static String cmdIndicator;
	private static MessageSystem messageSystem;
	
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
		
		//Create Message System
		messageSystem = new MessageSystem(cmdIndicator);
		messageSystem.setHighlights(loadedConfig.getProperty("highlights_enabled"));
		messageSystem.setHighlightsLevel(loadedConfig.getProperty("highlights_level"));
		messageSystem.setOauthToken(botOauth);
		
		//Create Points System
		pointsSystem = null;
		if (loadedConfig.getProperty("points_enabled") == "true") {
			pointsSystem = new PointsSystem();
		}
		
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
		Message msgReply = null;; 
		//if message starts with a command indicator, do shit
		if (message.startsWith(cmdIndicator)) {
			log.debug("Processing command '" + message + "' from user '" + user + "' who " + (channel.isMod(user) ? " is " : " isnt") + " mod" );
			msgReply = messageSystem.doCommand(channel, message, user);
			//Send the reply if there is one, and it is a whisper
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
			//They tried to do a command, for better or for worse, so no points for it
		} else {
			//Give them points if the points system is enabled
			if ((!(pointsSystem == null)) && channel.isLive()) {
				pointsSystem.givePoints(channel, user, "message");
			}
		}
		
	}
	
}
