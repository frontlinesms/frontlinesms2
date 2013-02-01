package frontlinesms2

class RecipientLookupService {
	static transactional = true
	def i18nUtilService
	
	def contactSearchResults(params) {
		def values = params.recipients
		def contacts = Contact.getAll(values.findAll { it.startsWith("contact-") }.collect { it.split("-", 2)[1].toLong() } )
		def groups = Group.getAll(values.findAll { it.startsWith("group-") }.collect { it.split("-", 2)[1].toLong() })
		def smartgroups = SmartGroup.getAll(values.findAll { it.startsWith("smartgroup-") }.collect { it.split("-", 2)[1].toLong() })
		return [contacts:contacts, groups:groups, smartgroups:smartgroups]
	}

	def lookup(queryString) {
		queryString = "%${queryString.toLowerCase()}%"
		def results = []
		def contactResults = []
		getContacts([searchString:queryString]).each{ contactResults << [value: "contact-${it.id}", text: it.name] }
		def groupResults = []
		Group.findAllByNameIlike(queryString).each { groupResults << [value: "group-${it.id}", text: it.name] } 
		def smartgroupResults = []
		SmartGroup.findAllByNameIlike(queryString).each { smartgroupResults << [value: "smartgroup-${it.id}", text: it.name] } 
		[(i18nUtilService.getMessage([code:"contact.search.groups"])) :groupResults, (i18nUtilService.getMessage([code:"contact.search.smartgroups"])) :smartgroupResults, (i18nUtilService.getMessage([code:"contact.search.contacts"])) :contactResults].each {
			if(it.value)
				results << [group: true, text:(it.key), items: it.value]
		}
		return results
	}
}
