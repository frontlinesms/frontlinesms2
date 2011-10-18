package frontlinesms2

class SmartGroupController {
	def create = {
		def smartGroupInstance = new SmartGroup()
		[smartGroupInstance: smartGroupInstance]
	}
	
	def save = {
		def smartGroupInstance = new SmartGroup()
		smartGroupInstance.name = params.smartgroupname
		getRuleText().eachWithIndex { ruleText, i ->
			def ruleField = getRuleField(i)
			assert ruleField in ['contactName', 'mobile', 'email', 'notes'] // prevent injection - these should match the sanctioned fields user can set
			smartGroupInstance."$ruleField" = ruleText
		}
		
		if(smartGroupInstance.save()) {
			println "Adding flash message..	."
			flash.message = "Created new smart group: '$params.smartgroupname'"
			println "Redirecting..."
			redirect controller:'contact', action:'show'
		} else render text: "Failed to save smart group<br/><br/>with params $params<br/><br/>errors: $smartGroupInstance.errors"
	}
	
	def show = {
		redirect(controller: "contact", action: "show", params:[smartGroupId : params.id])
	}
	
	private def getRuleText() {
		def t = params['rule-text']
		t instanceof List? t: [t]
	}
	
	private def getRuleField(i) {
		def f = params['rule-field']
		if(f instanceof List) return f[i]
		else {
			assert i == 0
			return f
		}
	}
}
