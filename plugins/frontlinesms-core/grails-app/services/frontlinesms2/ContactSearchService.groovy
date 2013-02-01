package frontlinesms2

class ContactSearchService {
	static transactional = true
	def i18nUtilService
	
	def contactList(params) {
		[contactInstanceList: getContacts(params),
				contactInstanceTotal: countContacts(params),
				contactsSection: params?.groupId? Group.get(params.groupId): params.smartGroupId? SmartGroup.get(params.smartGroupId): null]
	}

	def contactSearchResults(params) {
		def values = params.contactsearch
		def groups = smartgroups = contacts = addresses = []
		values.split(",").each { recipient ->
			def typeAndValue = recipient.split("-", 2)
			def type = typeAndValue[0]
			def value = typeAndValue[1]
		}
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
	
	private def getContacts(params) {
		def searchString = getSearchString(params)
		if(params.groupId) {
			GroupMembership.searchForContacts(asLong(params.groupId), searchString, params.sort,
					params.max,
			                params.offset)
		} else if(params.smartGroupId) {
			SmartGroup.getMembersByNameIlike(asLong(params.smartGroupId), searchString, [max:params.max, offset:params.offset])
		} else Contact.findAllByNameIlikeOrMobileIlike(searchString, searchString, params)
	}
	
	private def countContacts(params) {
		def searchString = getSearchString(params)
		
		if(params.groupId) {
			GroupMembership.countSearchForContacts(asLong(params.groupId), searchString)
		} else if(params.smartGroupId) {
			SmartGroup.countMembersByNameIlike(asLong(params.smartGroupId), searchString)
		} else Contact.countByNameIlikeOrMobileIlike(searchString, searchString)
	}
	
	private def getSearchString(params) {
		params.searchString? "%${params.searchString.toLowerCase()}%": '%'
	}
	
	private def asLong(Long v) { v }
	private def asLong(String s) { s.toLong() }
}
