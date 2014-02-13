package frontlinesms2

class RecipientLookupService {
	private static final int MAX_PER_SECTION = 3

	def contactSearchService
	def i18nUtilService
	
	// TODO rename this method
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

	private def ids(selectedList, clazz) {
		values(selectedList, clazz.shortName)*.toLong()?: [0L]
	}

	private def objects(selectedList, clazz) {
		clazz.getAll(ids(selectedList, clazz)) - null
	}

	def lookup(params) {
		def query = "%${params.term.toLowerCase()}%"
		def selectedSoFar = getSelectedSoFar(params)
		def results = [contact:lookupContacts(query, ids(selectedSoFar, Contact)),
				group:lookupGroups(query, ids(selectedSoFar, Group)),
				smartgroup:lookupSmartgroups(query, ids(selectedSoFar, SmartGroup))].collect { k, v ->
			if(v) [group:true, text:i18nUtilService.getMessage(code:"contact.search.$k"), items:v] } - null
		def strippedNumber = stripNumber(params.term)
		if (strippedNumber) {
			results << [group:true,
					text:i18nUtilService.getMessage([code:"contact.search.address"]),
					items:[[value: "address-$strippedNumber",
							text: "\"$strippedNumber\""]]]
		}
		return [query: params.term, results: results]
	}

	private def getSelectedSoFar(params) {
		def s = params.'selectedSoFar[]'
		if(s) {
			return s instanceof String? [s]: s
		}
		s = params.selectedSoFar
		return s && s!='null'? s: ''
	}

	private def stripNumber(mobile) {
		def n = mobile?.replaceAll(/\D/, '')
		if(mobile && mobile[0] == '+') n = '+' + n
		n
	}

	private def lookupContacts(query, alreadySelected=[]) {
		contactSearchService.getContacts([searchString:query, max:MAX_PER_SECTION, exclude:alreadySelected]).collect {
			[value: "contact-${it.id}", text: it.name] }
	}

	private def lookupGroups(query, alreadySelected=[]) {
		Group.findAllByNameIlikeAndIdNotInList(query, alreadySelected, [max:MAX_PER_SECTION]).collect {
			[value: "group-${it.id}", text: "$it.name (${it.countMembers()})"] }
	}

	private def lookupSmartgroups(query, alreadySelected=[]) {
		SmartGroup.findAllByNameIlikeAndIdNotInList(query, alreadySelected, [max:MAX_PER_SECTION]).collect {
			[value: "smartgroup-${it.id}", text: "$it.name (${it.countMembers()})"] }
	}

	def stripPrefix = { it.tokenize('-')[1] }

	def getContacts = { recipients ->
		[recipients].flatten().findAll { it.startsWith('contact') }.collect { Contact.get(stripPrefix(it)) }.findAll { it!=null }
	}

	def getGroups = { recipients ->
		[recipients].flatten().findAll { it.startsWith('group') }.collect { Group.get(stripPrefix(it)) }.flatten()
	}

	def getSmartGroups = { recipients ->
		[recipients].flatten().findAll { it.startsWith('smartgroup') }.collect { SmartGroup.get(stripPrefix(it)) }.flatten()
	}

	def getManualAddresses = { recipients ->
		log.info "############# $recipients"
		[recipients].flatten().findAll { it.startsWith('address') }.collect { stripPrefix(it) }
	}

	def getAddressesFromRecipientList(rawRecipients) {
		def recipients = [rawRecipients].flatten()
		def addresses = []
		def contactList = []
		def groupAddressList = []
		def smartGroupAddressList = []
		def manualAddressList = []

		contactList = getContacts(recipients)*.mobile.flatten() - null
		groupAddressList = getGroups(recipients)*.addresses.flatten() - null
		smartGroupAddressList = getSmartGroups(recipients)*.addresses.flatten() - null
		manualAddressList = getManualAddresses(recipients).flatten()

		addresses = contactList + groupAddressList + smartGroupAddressList + manualAddressList
		addresses.flatten().unique()
	}
}

