package frontlinesms2

// TODO please clean up this code's formatting
class ForwardActionStep extends Step {
	def grailsApplication
	def autoforwardService

	static service = 'autoforward'
	static action = 'doForward'
	static String getShortName() { 'forward' }

	static configFields = [sentMessageText: 'textarea', recipients: '']

	Map getConfig() {
		def config = [stepId:id, sentMessageText:sentMessageText]
		['contacts':'Contact', 'groups':'Group', 'smartGroups':'SmartGroup', 'addresses':'Address'].each { name, type ->
			config."$name" = getRecipientsByDomain(type)
		}
		config
        }

	def getSentMessageText() {
		getPropertyValue("sentMessageText")
	}

	def getRecipients() {
		def addresses = getRecipientsByDomain("Address")
		def groups = getRecipientsByDomain("Group")
		def smartGroups = getRecipientsByDomain("SmartGroup")
		def contacts = getRecipientsByDomain("Contact")

		groups.each { group-> addresses << group.members.collect{ it.mobile } }
		smartGroups.each { group-> addresses << group.members.collect{ it.mobile } }
		addresses << contacts.collect{ it.mobile }

		return addresses.flatten().unique()
	}

	def getRecipientsByDomain(domainName) {
		def addresses = []
		if(domainName == "Address") {
			stepProperties.collect { step->
				if(step.value.startsWith(domainName)) {
					addresses << step.value.substring(domainName.size() + 1, step.value.size())
				}
			}
			return addresses - null
		}

		// FIXME WTF is this doing?  Is this a dumb way of doing Class.forName(), or just passing the class to the method in the first place?
		def domain = grailsApplication.domainClasses*.clazz.find { (it.name - "frontlinesms2.") == domainName }
		def domainInstances = stepProperties.collect { step->
			if(step.value.startsWith(domainName)) {
				domain.get(step.value.substring(domainName.size() + 1, step.value.size()) as Long)
			}
		}
		domainInstances - null
	}

	def setRecipients(contacts, groups, smartGroups, addresses) {
		stepProperties.findAll { it.key == "recipient" }.each { removeFromStepProperties(it) }
		if(groups)
			setRecipientsBydomain("Group", groups)
		if(smartGroups)
			setRecipientsBydomain("SmartGroup", smartGroups)
		if(contacts)
			setRecipientsBydomain("Contact", contacts)
		if(addresses)
			setRecipientsBydomain("Address", addresses)
	}

	private def setRecipientsBydomain(domainName, instanceList) {
		instanceList.each {
			this.addToStepProperties(new StepProperty(key:"recipient", value:"${domainName}-${domainName == 'Address' ? it : it.id}")).save()
		}
	}
	
	def process(Fmessage message) {
		autoforwardService.doForward(this, message)
	}

	def getDescription() {
		i18nUtilService.getMessage(code:"customactivity.${this.shortName}.description", args:[this.sentMessageText])
	}

}
