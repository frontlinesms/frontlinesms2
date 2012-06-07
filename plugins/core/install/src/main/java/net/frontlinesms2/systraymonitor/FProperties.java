package net.frontlinesms2.systraymonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static net.frontlinesms2.systraymonitor.Utils.*;

class FProperties {
	private Properties properties;

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

	int getInt(String propertyName, int defaultValue) {
		String stringValue = properties.getProperty(propertyName);
		int intValue = defaultValue;
		if(stringValue != null) try {
			intValue = Integer.parseInt(stringValue);
		} catch(NumberFormatException _) {}
		return intValue;
	}
}

