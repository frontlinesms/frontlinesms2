package frontlinesms2

class SmartGroupController {
	private static final String CUSTOM_FIELD_ID_PREFIX = 'custom:'
	
	def create = {
		def smartGroupInstance = new SmartGroup()
		def customFieldNames = CustomField.allUniquelyNamed
		[smartGroupInstance:smartGroupInstance,
				fieldNames:['Phone number', 'Contact name', 'email', 'notes']+customFieldNames,
				fieldIds:['mobile', 'contactName', 'email', 'notes']+customFieldNames.collect { CUSTOM_FIELD_ID_PREFIX+it }]
	}
	
	def save = {
		def smartGroupInstance = new SmartGroup()
		smartGroupInstance.name = params.smartgroupname
		getRuleText().eachWithIndex { ruleText, i ->
			def ruleField = getRuleField(i)
			if(ruleField.startsWith(CUSTOM_FIELD_ID_PREFIX)) {
				ruleField = ruleField.replaceFirst(CUSTOM_FIELD_ID_PREFIX, '')
				smartGroupInstance.addToCustomFields(new CustomField(name:ruleField, value:ruleText))
			} else {
				assert ruleField in ['contactName', 'mobile', 'email', 'notes'] // prevent injection - these should match the sanctioned fields user can set
				smartGroupInstance."$ruleField" = ruleText
			}
		}
		
		if(smartGroupInstance.save()) {
			flash.message = "Created new smart group: '$params.smartgroupname'"
			redirect controller:'contact', action:'show'
		} else {
			render text: "Failed to save smart group<br/><br/>with params $params<br/><br/>errors: $smartGroupInstance.errors"
		}
	}
	
	def show = {
		redirect(controller: "contact", action: "show", params:[smartGroupId:params.id])
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
