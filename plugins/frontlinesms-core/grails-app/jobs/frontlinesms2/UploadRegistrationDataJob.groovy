package frontlinesms2

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

class UploadRegistrationDataJob {

	File regPropFile
	def dataUploadService

	static final String UPLOAD_URL = "http://register.frontlinesms.com/process/"
	static final String REGISTRATION_FILE = 'registration.properties'
	

	static triggers = {
		//simple repeatInterval: 5000l // execute job once in 5 seconds
		cron name: 'RegistrationTrigger',cronExpression: '0 0 6-18 ? 1-12 MON'
	}

	def execute() {
		Properties properties = getRegistrationProperties()
		if(!properties || properties?.isEmpty()){
			return //there is no registration data to send
		}
		def dataMap = convertPropertiestoMap(properties)
		def registered = (dataMap['registered'] == 'true')?:false
		if(registered) {
			return //registration data has already been uploaded
		}		
		try{
			dataUploadService.upload(UPLOAD_URL,dataMap)
			writeRegistrationPropertiesFile(properties)
		}catch(Exception e){
			e.printStackTrace()
			throw new org.quartz.JobExecutionException(e)
		}	
	}

	def getRegistrationProperties() {
		regPropFile = new File(ResourceUtils.resourceDirectory, REGISTRATION_FILE)
		if(!regPropFile.exists())
			return null
		Properties properties = new Properties()
		regPropFile.withInputStream { stream -> properties.load(stream) }
		properties
	}

	def writeRegistrationPropertiesFile(Properties properties) {
		properties.setProperty("registered",'true');
		properties.store(new OutputStreamWriter(new FileOutputStream(regPropFile), "UTF-8"),null);
	}

	def convertPropertiestoMap(Properties p) {
		def m = [:]
		p.each { k, v -> m[k] = v }
		return m
	}
}
