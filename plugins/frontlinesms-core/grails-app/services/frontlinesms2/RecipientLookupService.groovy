package frontlinesms2

class RecipientLookupService {
	def contactSearchService
	def i18nUtilService
	
	def contactSearchResults(params) {
		def selectedList = params.recipients
		def contacts = objects(selectedList, Contact)
		def groups = objects(selectedList, Group)
		def smartgroups = objects(selectedList, SmartGroup)
		def addresses = values(selectedList, 'address')
		return [contacts:contacts, groups:groups, smartgroups:smartgroups, addresses:addresses]
	}

	private def values(selectedList, shortName) {
		selectedList.findAll { it.startsWith "$shortName-" }.collect {
			it.split('-', 2)[1]
		}
	}

	private def objects(selectedList, clazz) {
		clazz.getAll(values(selectedList, clazz.shortName)*.toLong())
	}

	def lookup(originalQuery) {
		def query = "%${originalQuery.toLowerCase()}%"
		def results = [contacts:lookupContacts(query),
				groups:lookupGroups(query),
				smartgroups:lookupSmartgroups(query)].collect { k, v ->
			if(v) [group:true, text:i18nUtilService.getMessage(code:"contact.search.$k"), items:v] } - null
		def strippedNumber = stripNumber(originalQuery)
		if (strippedNumber)
			results << [group:true, text: i18nUtilService.getMessage([code:"contact.search.address"]), items: [[value: "address-$strippedNumber", text: "\"$strippedNumber\""]]]
		return results
	}

	private def stripNumber(mobile) {
		def n = mobile?.replaceAll(/\D/, '')
		if(mobile && mobile[0] == '+') n = '+' + n
		n
	}

	private def lookupContacts(query) {
		contactSearchService.getContacts([searchString:query]).collect {
			[value: "contact-${it.id}", text: it.name] }
	}

	private def lookupGroups(query) {
		Group.findAllByNameIlike(query).collect {
			[value: "group-${it.id}", text: it.name] }
	}

	private def lookupSmartgroups(query) {
		SmartGroup.findAllByNameIlike(query).collect {
			[value: "smartgroup-${it.id}", text: it.name] }
	}
}

