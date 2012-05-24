package net.frontlinesms.install4j.custom;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class Testsend {
	public boolean submitData(Map<String, String> data){
		CompleteRegistration completeReg = new CompleteRegistration();
		String uuid;
		try{
			uuid = completeReg.generateUUID()
			data.put("UUID", uuid);
			URL siteUrl = new URL("http://register.frontlinesms.com/process.php");
			HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			
			Set keys = data.keySet();
			Iterator keyIter = keys.iterator();
			String content = "";
			for(int i=0; keyIter.hasNext(); i++) {
				Object key = keyIter.next();
				if(i!=0) {
					content += "&";
				}
				content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
			}
			System.out.println(content);
			out.writeBytes(content);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while((line=in.readLine())!=null) {
				System.out.println(line);
			}
			in.close();
			completeReg.createRegistrationPropertiesFile(uuid, true);
			return true;
		}catch(Exception e){
			completeReg.createRegistrationPropertiesFile(uuid, true);
			return false;
		}
	}
}

