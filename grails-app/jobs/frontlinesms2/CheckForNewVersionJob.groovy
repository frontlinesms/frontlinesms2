package frontlinesms2

class CheckForNewVersionJob {
	private final String VERSION_URL = 'http://dev.frontlinesms.com/2/latest'
	private final String DOWNLOAD_URL = 'http://dev.frontlinesms.com/2/latest/download'
	
	static triggers = {
		simple name:'newVersionCheck', startDelay:1000, repeatInterval:30*60*1000
		// This second trigger is required to make sure that the job is run at startup, as there seems to be
		// some issue with startDelay being related to repeatInterval rather than specified separately.
		// This *can* cause the job to fire twice at startup, but that's better than not firing at all.
		simple name:'initialNewVersionCheck', startDelay:1000, repeatInterval:10, repeatCount:0
	}
	
	def applicationVersionService
	def notificationService

	def execute() {
		println "Running update job at ${new Date()}"
		try {
			def latestAvailable = new URL(VERSION_URL).text
			println "Latest version: $latestAvailable"
			if(applicationVersionService.shouldUpgrade(latestAvailable)) {
				println "Should upgrade - creating sys notification"
				new SystemNotification(text:"There is a new version of FrontlineSMS available, <a href=\"$DOWNLOAD_URL\">click here to download it now</a>.").save(failOnError:true)
			}
		} catch(Exception ex) {
			println "Exception while checking latest version"
			ex.printStackTrace()
			log.info("Could not complete version check for url $VERSION_URL.", ex);
		}
	}
}
