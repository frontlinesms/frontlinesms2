package frontlinesms2

class SmartGroupController {
	def create = {
		def smartGroupInstance = new SmartGroup()
		smartGroupInstance.properties = params
		[smartGroupInstance: smartGroupInstance]
	}
}
