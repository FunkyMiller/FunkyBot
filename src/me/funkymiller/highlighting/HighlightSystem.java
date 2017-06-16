package me.funkymiller.highlighting;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;

import me.funkymiller.Core.FunkyBot;

public class HighlightSystem {
	
	private FunkyBot bot;

	public void addHighlight(Channel channel, User user, String comment) {
		Long highlightTime = System.currentTimeMillis();
		channel.getGoLiveStart(channel, bot);
	}
	
}
