package net.frontlinesms.install4j.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckRegistration {
	public boolean isRegistered() throws IOException {
		File regPropFile = Futil.getRegistrationPropertiesFile();
		if (regPropFile.exists()) {
			return extractIsRegistered(regPropFile);
		} else {
			return false;
		}
	}

	private static String getKey(String line) {
		return line.split("=")[0];
	}
	
	private static String getValue(String line) {
		String[] parts = line.split("=");
		return parts.length>1? parts[1]: null;
	}

	private static boolean extractIsRegistered(File regPropFile) throws IOException {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader in = null;
		try {
			fis = new FileInputStream(regPropFile);
			isr = new InputStreamReader(fis, "UTF-8");
			in = new BufferedReader(isr);
			return extractIsRegistered(in);
		} finally {
			// Close all streams safely
			try { fis.close(); } catch(Exception _) { /* ignore */ }
			try { isr.close(); } catch(Exception _) { /* ignore */ }
			try { in.close(); } catch(Exception _) { /* ignore */ }
		}
	}

	// TODO unit test this method
	static boolean extractIsRegistered(BufferedReader in) throws IOException {
		// TODO separate this into separate file and unit test
		String registrationStatus = null;
		String line;
		while (registrationStatus == null && (line = in.readLine()) != null) {
			if(getKey(line).equals("register")) {
				registrationStatus = getValue(line);
			}
		}
		return registrationStatus != null && registrationStatus.trim().equals("true");
	}
}

