package frontlinesms2

class ContactSearchService {
	static transactional = true
	
	def contactList(params) {
		[contactInstanceList: getContacts(params),
				contactInstanceTotal: countContacts(params),
				contactsSection: params.groupId? Group.get(params.groupId): params.smartGroupId? SmartGroup.get(params.smartGroupId): null]
	}
	
	private def getContacts(params) {
		def searchString = getSearchString(params)
		
		if(params.groupId) {
			GroupMembership.searchForContacts(asLong(params.groupId), searchString,
					params.max,
			                params.offset)
		} else if(params.smartGroupId) {
			SmartGroup.getMembersByNameIlike(asLong(params.smartGroupId), searchString, [max:params.max, offset:params.offset])
		} else Contact.findAllByPrimaryMobileIlike(searchString, params)
	}
	
	private def countContacts(params) {
		def searchString = getSearchString(params)
		
		if(params.groupId) {
			GroupMembership.countSearchForContacts(asLong(params.groupId), searchString)
		} else if(params.smartGroupId) {
			SmartGroup.countMembersByPrimaryMobileIlike(asLong(params.smartGroupId), searchString)
		} else Contact.countByPrimaryMobileIlike(searchString)
	}
	
	private def getSearchString(params) {
		params.searchString? "%${params.searchString.toLowerCase()}%": '%'
	}
	
	private def asLong(Long v) { v }
	private def asLong(String s) { s.toLong() }
}
