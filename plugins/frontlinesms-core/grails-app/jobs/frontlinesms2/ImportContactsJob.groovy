package frontlinesms2

class ImportContactsJob {
	def grailsApplication

	def execute(context) {
		def importService =  grailsApplication.mainContext.importService
		def jobData =  context.mergedJobDataMap
		def fileType = jobData.get('fileType')
		def params = jobData.get('params')
		def request = jobData.get('request')

		if(fileType == 'csv') {
			importService.importContactCsv(params, request)
		} else {
			importService.importContactVcard(params, request)
		}
	}

}
