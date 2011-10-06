package frontlinesms2

class SmartGroupController {
	def create = {
		def smartGroupInstance = new SmartGroup()
		[smartGroupInstance: smartGroupInstance]
	}
	
	def save = {
		def smartGroupInstance = new SmartGroup()
		smartGroupInstance.name = params.smartgroupname
		params.rules.each {
			println "Rule: $it"
		}
		
		println "Adding flash message..."
		flash.message = "Created new smart group: '$params.smartgroupname'"
		println "Redirecting..."
		redirect controller:'contact', action:'show'
	}
}
