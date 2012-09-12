package net.frontlinesms2.systraymonitor.system;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;

public class UnixUser {
	private String username;
	private List<String> groups;

//> ACCESSORS
	public String getUsername() { return username; }
	public boolean isMemberOf(String group) {
		return groups.contains(group);
	}

//> ACTIONS
	public void addToGroup(String group) {
		log("addToGroup('" + group + "') :: ENTRY");
		new ProcessExecutor() {
			public void doExecute(BufferedReader reader) throws IOException {
				echoOutput(reader);
			}
		}.execute("sudo", "usermod", "-a", "-G", group, this.username);
		log("addToGroup() :: EXIT");
	}

//> INITIALISERS
	public void initUsername() {
		log("initUsername() :: ENTRY");
		new ProcessExecutor() { public void doExecute(BufferedReader reader) throws IOException {
			UnixUser.this.username = reader.readLine();
		}}.execute("id", "-un");
		log("initUsername() :: read username as '" + this.username + "'");
	}

	public void initGroups() {
		log("initGroups() :: ENTRY");
		new ProcessExecutor() {
			public void doExecute(BufferedReader reader) throws IOException {
				UnixUser.this.groups = readGroups(reader);
			}
		}.execute("groups");
		log("initGroups() :: Groups initialised to: " + this.groups);
	}

//> PRIVATE HELPERS
	private List<String> readGroups(BufferedReader bufferedReader) throws IOException {
		List<String> groups = new LinkedList<String>();
		String line;
		while((line = bufferedReader.readLine()) != null) {
			for(String group : line.split("\\s")) {
				groups.add(group);
			}
		}
		return groups;
	}

	private void log(String message) {
		// System.out.println("UnixUser." + message);
	}
}

