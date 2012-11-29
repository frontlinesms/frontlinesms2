package net.frontlinesms.install4j.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


class AppSettingsConfig {
	public static void setAppProperty(String key, String value){
		File propertiesFile = getPropertiesFile("app-settings.properties");
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(regPropFile), "UTF-8"));
			properties.setProperty(key,value);
			properties.store(new OutputStreamWriter(new FileOutputStream(regPropFile), "UTF-8"),null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getAppProperty(String key){
		File propertiesFile = getPropertiesFile("app-settings.properties");
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8"));
			return properties.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File getPropertiesFile(String fileName) throws IOException {
		File frontlinesms2Directory = Futil.getResourceDirectory();
		if(!frontlinesms2Directory.exists()) frontlinesms2Directory.mkdirs();
		File propertiesFile = new File(frontlinesms2Directory, fileName);
		try {
			if(!propertiesFile.exists()) {
				propertiesFile.createNewFile();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return propertiesFile;
	}
}