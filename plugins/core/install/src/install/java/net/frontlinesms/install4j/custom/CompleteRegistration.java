package net.frontlinesms.install4j.custom;
import java.util.UUID;

public class CompleteRegistration {
	public void createRegistrationPropertiesFile(String uuid, boolean success){
		File userHome = new File(System.getProperty("user.home"));
		File regProp = new File(userHome.getAbsolutePath()+"/.fronlinesms2/registration.properties");
		
		BufferedWriter wt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(regProp)));
		
		if(success){
			wt.write("registered=true");
			wt.write("uuid=" + uuid);
		} else {
			wt.write("registered=false");
		}
		wt.close();		
		
	}

	public String generateUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
