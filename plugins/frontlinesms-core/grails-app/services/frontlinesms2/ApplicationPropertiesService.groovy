package frontlinesms2

class ApplicationPropertiesService {
	def propertyFileLocation = ResourceUtils.resourceDirectory.absolutePath + "/features.properties"
	def showPopupInCurrentSession = false
//getters
	def getLastVersionRun(){
		return getApplicationProperty('last.version.run')?:null
	}

	def getShowNewFeaturesPopup(){
		return (getApplicationProperty('show.new.features.popup') == "true")?true:false
	}

//setters
	def setLastVersionRun(value){
		storeProperty("last.version.run",value)
	}

	def setShowNewFeaturesPopup(value){
		storeProperty("show.new.features.popup",value)	
	}

	private void storeProperty(key,value){
		def properties = new Properties()

		File regPropFile = new File(propertyFileLocation)
		FileInputStream fis
		InputStreamReader isr
		try {
			fis = new FileInputStream(regPropFile)
			isr = new InputStreamReader(fis, "UTF-8")
			properties.load(isr)
		} catch(Exception ex) {
			ex.printStackTrace()
		} finally {
			if(isr != null) try { isr.close() } catch(Exception _) { /* ignore */ }
			if(fis != null) try { fis.close() } catch(Exception _) { /* ignore */ }
		}

		properties.setProperty(key, value.toString())

		FileOutputStream fos
		OutputStreamWriter osw
		try {
			fos = new FileOutputStream(regPropFile)
			osw = new OutputStreamWriter(fos, "UTF-8")
			properties.store(osw, null)
		} catch(Exception ex) {
			ex.printStackTrace()
		} finally {
			if(osw != null) try { osw.close() } catch(Exception _) { /* ignore */ }
			if(fos != null) try { fos.close() } catch(Exception _) { /* ignore */ }
		}
	}

	def getApplicationProperty(key){
		def MATCHER = key.replace('.', /\./) + /=(.*)/
		def match
		def file = new File(propertyFileLocation)
		if (file.exists()) {
			file.eachLine {
				if(it ==~ MATCHER) match = (it =~ MATCHER)[0][1]
			}
		}
		return match
	}
}