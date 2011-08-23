package frontlinesms2

class ArchiveController extends MessageController {
	def beforeInterceptor = {
		params['max'] = params['max'] ?: getPaginationCount()
		params['offset']  = params['offset'] ?: 0
		params['archived'] = true
		true
	}
}
