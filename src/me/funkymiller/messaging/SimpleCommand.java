package me.funkymiller.messaging;

public class SimpleCommand {

	private String command;
	private String reply;
	private String level;
	private Integer modCool;
	private Integer subCool;
	private Integer follCool;
	private Integer plebCool;
	
	public SimpleCommand(String cmd, String rep) {
		command = cmd;
		reply = rep;
		level = "A";
		modCool = 10;
		subCool = 30;
		follCool = 60;
		plebCool = 120;
	}
	
	public SimpleCommand(String cmd, String rep, String lev) {
		command = cmd;
		reply = rep;
		level = lev.toUpperCase();
		modCool = 10;
		subCool = 30;
		follCool = 60;
		plebCool = 120;
	}
	public SimpleCommand(String cmd, String rep, String lev, Integer mod, Integer sub, Integer foll, Integer pleb) {
		command = cmd;
		reply = rep;
		level = lev.toUpperCase();
		modCool = mod;
		subCool = sub;
		follCool = foll;
		plebCool = pleb;
	}
	
	public String getCommand() {
		return command;
	}

	public String getReply() {
		return reply;
	}

	public String getLevel() {
		return level;
	}

	public Integer getModCool() {
		return modCool;
	}

	public Integer getSubCool() {
		return subCool;
	}

	public Integer getFollCool() {
		return follCool;
	}

	public Integer getPlebCool() {
		return plebCool;
	}
}
