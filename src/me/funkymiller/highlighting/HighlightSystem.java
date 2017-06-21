package me.funkymiller.highlighting;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;

import me.funkymiller.Core.FunkyBot;

public class HighlightSystem {
	
	private FunkyBot bot;
	
	//Highlighting variables
	private boolean highlightsOn;
	private String highlightsLevel;	//C for Caster, M for Mod, S for Sub, R for Regular, F for Follower, A for All
	
	public HighlightSystem(FunkyBot botIn) {
		bot = botIn;
		highlightsOn = true;
		highlightsLevel = "M";
	}

	public void addHighlight(Channel channel, User user, String comment) {
		Long highlightTime = System.currentTimeMillis();
		Long vodStartTime = Channel.getGoLiveStart(channel, bot);
		Highlight thisHL = new Highlight(channel, highlightTime, vodStartTime);
	}
	
	public void setHighlights(boolean hlStatus) {
		if (hlStatus) {
			highlightsOn = true;
			highlightsLevel= "M";
		} else {
			highlightsOn = false;
			highlightsLevel = null;
		}
	}
	
	public void setHighlights(String hlStatus) {
		if (hlStatus.equalsIgnoreCase("true") || hlStatus.equalsIgnoreCase("on")) {
			highlightsOn = true;
			highlightsLevel= "M";
		} else {
			highlightsOn = false;
			highlightsLevel = null;
		}
	}
	
	public boolean isHighlightsOn() {
		return highlightsOn;
	}
	
	
	public void setHighlightsLevel(String lev) {
		switch (lev.toUpperCase()) {
		case "C":
		case "M":
		case "R":
		case "S":
		case "F":
		case "A":
			highlightsLevel = lev.toUpperCase();
			break;
		default:
			highlightsLevel = "M";
			break;
		}
	}
	
	public String getHighlightsLevel() {
		return highlightsLevel;
	}
	
}
