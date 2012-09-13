package net.frontlinesms2.systraymonitor.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/** N.B. this class is not thread-safe */
public abstract class CommandlineUserInteraction {
	private BufferedReader reader;
	public void echo(String message) {
		System.out.println(message);
	}

	public String prompt() {
		try {
			if(reader == null) {
				reader = new BufferedReader(new InputStreamReader(System.in));
			}
			return reader.readLine();
		} catch(IOException _) {
			return null;
		}
	}

	public char charPrompt(char defaultResponse) {
		String response = prompt();
		if(response == null || response.length() == 0) return defaultResponse;
		else return response.charAt(0);
	}
}

