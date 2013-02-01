package frontlinesms2

class UploadRegistrationDataJob {

	File regPropFile
	def dataUploadService

	static final String UPLOAD_URL = "http://register.frontlinesms.com/process/"
	static final String REGISTRATION_FILE = 'registration.properties'
	

	static triggers = {
		long week = 7 * 24 * 3600 * 1000 // execute job once in 7 days
		simple name:'RegistrationUpload', startDelay:0, repeatInterval:week, repeatCount:1
	}

	def execute() {
		println "UploadRegistrationDataJob: Attempting to upload registration data..."
		Properties properties = getRegistrationProperties()
		if(!properties || properties?.isEmpty()){
			println "SKIPPED : Registration data not available!"
			return //there is no registration data to send
		}
		def dataMap = convertPropertiestoMap(properties)
		def registered = (dataMap['registered'] == 'true')?:false
		if(registered) {
			println "SKIPPED : Registration data has already been uploaded!"
			return //registration data has already been uploaded
		}		
		try{
			boolean success = dataUploadService.upload(UPLOAD_URL,dataMap)
			if(!success){
				println "FAILED : Registration data upload NOT successful, check your Internet connection!"
				return
			}
			writeRegistrationPropertiesFile(properties)
			println "SUCCESS : Successfully uploaded registration data!"
		}catch(Exception e){
			println "FAILED : Registration data upload NOT successful, check your Internet connection!"
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
