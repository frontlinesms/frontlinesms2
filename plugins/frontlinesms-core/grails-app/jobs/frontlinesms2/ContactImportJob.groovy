package frontlinesms2

class ContactImportJob {
	def grailsApplication

	def execute(context) {
		def contactImportService =  grailsApplication.mainContext.contactImportService
		def jobData =  context.mergedJobDataMap
		def fileType = jobData.get('fileType')
		def params = jobData.get('params')
		def request = jobData.get('request')

		if(fileType == 'csv') {
			contactImportService.importContactCsv(params, request)
		} else {
			contactImportService.importContactVcard(params, request)
		}
	}

}
