package net.frontlinesms2.systraymonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static net.frontlinesms2.systraymonitor.Utils.*;

class FProperties {
//> INSTANCE VARIABLES
	private Properties properties;

//> CONSTRUCTORS
	FProperties(String file) {
		File f = new File(file);
		o("Attempting to load properties from: " + f.getAbsolutePath());
		properties = new Properties();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis, "UTF-8");
			properties.load(isr);
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(isr != null) try { isr.close(); } catch(Exception ex) { /* ignore */ }
			if(fis != null) try { fis.close(); } catch(Exception ex) { /* ignore */ }
		}
	}

//> PROPERTY GETTERS
	boolean getBoolean(String key) {
		String stringValue = properties.getProperty(key);
		return Boolean.parseBoolean(stringValue);
	}

	int getInt(String key) {
		String stringValue = properties.getProperty(key);
		return Integer.parseInt(stringValue);
	}

//> PROPERTY SETTERS
	void set(String key, String value) {
		properties.setProperty(key, value);
	}

	void set(String key, boolean value) {
		set(key, Boolean.toString(value));
	}

	void set(String key, int value) {
		set(key, Integer.toString(value));
	}

//> PROPERTY DEFAULT SETTERS
	void setDefault(String key, boolean value) {
		if(notSet(key)) set(key, value);
	}

	void setDefault(String key, int value) {
		if(notSet(key)) set(key, value);
	}

//> PRIVATE HELPER METHODS
	private boolean notSet(String key) {
		return properties.getProperty(key) == null;
	}
}

