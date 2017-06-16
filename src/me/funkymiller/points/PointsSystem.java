package me.funkymiller.points;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;

public class PointsSystem {
	private HashMap<User,Integer> pointsMap;
	private Logger log;
	private Integer ptsForMessage = 0;
	private Integer ptsForSub = 0;
	private Integer ptsForDonation = 0;
	private Integer ptsForBits = 0;
	
	public PointsSystem() {
		log = LoggerFactory.getLogger(getClass());
		loadPoints();
	}

	public void givePoints(Channel channel, User user, String ptsReason) {
		Integer pts = 0;
		switch (ptsReason.toLowerCase()) {
		case "message":
			pts = ptsForMessage;
			break;
		case "sub":
			pts = ptsForSub;
			break;
		case "donation":
			pts = ptsForDonation;
			break;
		case "bits":
			pts = ptsForBits;
			break;
		}
		Integer currPts = 0;
		if (pointsMap.containsKey(user)) {
			currPts = pointsMap.get(user);
		} 
		pointsMap.put(user, currPts + Math.abs(pts));
		log.debug("Added " + Math.abs(pts) + " to user '" + user + "' for channel '" + channel + "'");
	}
	
	public void takePoints(Channel channel, User user, Integer pts) {
		Integer currPts = 0;
		if (pointsMap.containsKey(user)) {
			currPts = pointsMap.get(user);
		} 
		pointsMap.put(user, currPts - Math.abs(pts));
		log.debug("Deducted " + Math.abs(pts) + " to user '" + user + "' for channel '" + channel + "'");
	}
	
	public void loadPoints() {
		
	}
	
	public void savePoints() {
		
	}
}
