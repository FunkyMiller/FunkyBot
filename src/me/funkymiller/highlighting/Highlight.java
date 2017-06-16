package me.funkymiller.highlighting;

import com.cavariux.twitchirc.Chat.Channel;

public class Highlight {

	private Channel channel;
	private Long timestamp;
	private Long streamStart;
	private String timeInVOD;
	
	public Highlight(Channel chan, Long ts, Long start) {
		channel = chan;
		timestamp = ts;
		streamStart = start;
		timeInVOD = convertToTime(start, ts);
	}
	
	private String convertToTime(Long start, Long ts) {
		String tsVOD = "";
		
		return tsVOD;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Long getStreamStart() {
		return streamStart;
	}

	public String getTimeInVOD() {
		return timeInVOD;
	}
}
