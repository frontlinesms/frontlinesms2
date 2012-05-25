package net.frontlinesms.install4j.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckRegistration {
	private File userHome;
	private File regProp;
	BufferedReader in;
	String uuid;
	String registrationStatus;

	public boolean isRegistered() throws IOException {
		boolean b;
		userHome = new File(System.getProperty("user.home"));
		regProp = new File(userHome.getAbsolutePath()
				+ "/.frontlinesms2/registration.properties");
		if (regProp.exists()) {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					regProp)));
			String line;
			while ((line = in.readLine()) != null) {
				registrationStatus = getValue(line);
				line = in.readLine();
				uuid = getValue(line);
			}
			if (registrationStatus.trim().equals("true")) {
				b = true;
			}else{
				b = false;
			}
		}else{
			b = false;
		}
		return b;
	}
	
	public String getValue(String tmp) {
		return tmp.split("=")[1];
	}
}
