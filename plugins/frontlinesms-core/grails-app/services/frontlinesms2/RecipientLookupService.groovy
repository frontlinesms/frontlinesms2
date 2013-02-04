package frontlinesms2

class RecipientLookupService {
	static transactional = true
	def contactSearchService
	def i18nUtilService
	
	def contactSearchResults(params) {
		def values = params.recipients
		def contacts = Contact.getAll(values.findAll { it.startsWith("contact-") }.collect { it.split("-", 2)[1].toLong() } )
		def groups = Group.getAll(values.findAll { it.startsWith("group-") }.collect { it.split("-", 2)[1].toLong() })
		def smartgroups = SmartGroup.getAll(values.findAll { it.startsWith("smartgroup-") }.collect { it.split("-", 2)[1].toLong() })
		def addresses = values.findAll { it.startsWith("address-") }.collect { it.split("-", 2)[1] }
		return [contacts:contacts, groups:groups, smartgroups:smartgroups, addresses:addresses]
	}

	def lookup(queryString) {
		def searchServiceQueryString = "%${queryString.toLowerCase()}%"
		def results = []
		def contactResults = []
		contactSearchService.getContacts([searchString:searchServiceQueryString]).each{ contactResults << [value: "contact-${it.id}", text: it.name] }
		def groupResults = []
		Group.findAllByNameIlike(searchServiceQueryString).each { groupResults << [value: "group-${it.id}", text: it.name] } 
		def smartgroupResults = []
		SmartGroup.findAllByNameIlike(searchServiceQueryString).each { smartgroupResults << [value: "smartgroup-${it.id}", text: it.name] } 
		[(i18nUtilService.getMessage([code:"contact.search.groups"])) :groupResults, (i18nUtilService.getMessage([code:"contact.search.smartgroups"])) :smartgroupResults, (i18nUtilService.getMessage([code:"contact.search.contacts"])) :contactResults].each {
			if(it.value)
				results << [group: true, text:(it.key), items: it.value]
		}
		def strippedNumber = stripNumber(queryString)
		if (strippedNumber)
			results << [group:true, text: i18nUtilService.getMessage([code:"contact.search.address"]), items: [[value: "address-$strippedNumber", text: "\"$strippedNumber\""]]]
		return results
	}

	private def stripNumber(mobile) {
		def n = mobile?.replaceAll(/\D/, '')
		if(mobile && mobile[0] == '+') n = '+' + n
		n
	}
}
