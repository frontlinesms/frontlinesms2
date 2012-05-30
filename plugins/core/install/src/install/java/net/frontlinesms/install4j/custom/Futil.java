package net.frontlinesms.install4j.custom;

import java.util.UUID;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Properties;

public class Futil {
//> CONSTANTS
	static final String PROCESSING_URL = "http://register.frontlinesms.com/process/";
	private static final String URL_REGEX = "(((file|gopher|news|nntp|telnet|http|ftp|https|ftps|sftp)://)|(www\\.))+(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?";
	private static final String EMAIL_REGEX = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z])*\\.)+[a-zA-Z]{2,9})$";

//> PUBLIC UTILITY METHODS
	public static boolean validateUrl(String url) {
		return url.matches(URL_REGEX);
	}

	public static boolean validateEmailAddress(String email) {
		return email.matches(EMAIL_REGEX);
	}

	public static void showAlert(String message) {
	    javax.swing.JOptionPane.showMessageDialog(null, message);
	}

//> PACKAGE UTILITY METHODS
	static File getRegistrationPropertiesFile() {
		File frontlinesms2Directory = new File(System.getProperty("user.home"), ".frontlinesms2");
		if(!frontlinesms2Directory.exists()) frontlinesms2Directory.mkdirs();
		File regPropFile = new File(frontlinesms2Directory, "registration.properties");
		try{
			if(!regPropFile.exists()) {
					regPropFile.createNewFile();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		return regPropFile;
	}

	static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	static void log(String s) {
		System.out.println("\t: "+s);
	}

	public static void setRegistered(String value){
		Properties properties = new Properties();
		File regPropFile = Futil.getRegistrationPropertiesFile();
		try {
			properties.load(new InputStreamReader(new FileInputStream(regPropFile), "UTF-8"));
			properties.setProperty("registered",value);
			properties.store(new OutputStreamWriter(new FileOutputStream(regPropFile), "UTF-8"),null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createRegistrationPropertiesFile(String uuid, boolean registered ){
		createRegistrationPropertiesFile(uuid, registered , null);
	}

	static void createRegistrationPropertiesFile(String uuid, boolean registered , Map<String, String> data) {
		File regPropFile = Futil.getRegistrationPropertiesFile();
		String status = "";
		status = registered ? "true":"false";
		try {
			Properties properties = new Properties();
			properties.load(new InputStreamReader(new FileInputStream(regPropFile), "UTF-8"));
			properties.setProperty("registered",status);
			if (data != null){
				for(Entry<String, String> e: data.entrySet()) {
					properties.setProperty(e.getKey(),e.getValue());
				}
			}
			properties.store(new OutputStreamWriter(new FileOutputStream(regPropFile), "UTF-8"),null);

		} catch(IOException e) {
			e.printStackTrace();
		}
	}
//> PRIVATE UTILITY METHODS
}

