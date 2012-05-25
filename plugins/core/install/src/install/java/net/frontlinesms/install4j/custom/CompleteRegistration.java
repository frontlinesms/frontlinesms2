package net.frontlinesms.install4j.custom;

import java.util.UUID;
import java.io.*;

public class CompleteRegistration {
	public void createRegistrationPropertiesFile(String uuid, boolean success){
		File userHome = new File(System.getProperty("user.home"));
		File regProp = new File(userHome.getAbsolutePath()+"/.frontlinesms2/registration.properties");
		try{
			if(!regProp.exists()){
				regProp.createNewFile();
			}
			BufferedWriter wt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(regProp)));

			if(success){
				wt.write("registered=true\n");
				wt.write("uuid=" + uuid);
			} else {
				wt.write("registered=false");
			}
			wt.close();
		} catch(IOException e) {}
	}

	public String generateUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
