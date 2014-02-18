package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.JSON

class SearchController extends MessageController {
	def recipientLookupService
	def contactSearchService
	def textMessageService

	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.sort = params.sort ?: 'date'
		return true
	}
	
	def index() { redirect(action:'no_search', params:params) }
	
	def no_search() {
		[groupInstanceList:Group.findAll(),
				folderInstanceList:Folder.findAll(),
				activityInstanceList:Activity.findAll(),
				messageSection:'result',
				customFieldList:CustomField.getAllUniquelyNamed()]
	}

	def result() {
		def search = withSearch { searchInstance ->
			def activity =  getActivityInstance()
			searchInstance.owners = activity ? [activity] : null
			searchInstance.searchString = params.searchString ?: ""
			searchInstance.contactString = params.contactString ?: null
			searchInstance.group = params.groupId ? Group.get(params.groupId) : null
			searchInstance.status = params.messageStatus ?: null
			searchInstance.activityId = params.activityId ?: null
			searchInstance.inArchive = params.activityId || params.inArchive
			searchInstance.startDate = params.startDate ?: null
			searchInstance.endDate = params.endDate ?: null
			searchInstance.customFields = [:]
			searchInstance.starredOnly = params.starred?: false
			CustomField.getAllUniquelyNamed().each { customFieldName ->
				if(params[customFieldName])
					searchInstance.customFields[customFieldName] = params[customFieldName]
			}
			searchInstance.save(failOnError:true, flush:true)
		}

		def searchResultsList = textMessageService.search(search, params)

		[searchDescription:getSearchDescription(search), search:search,
				checkedMessageCount:params.checkedMessageList?.tokenize(',')?.size(),
				interactionInstanceList:searchResultsList.interactionInstanceList,
				interactionInstanceTotal:searchResultsList.interactionInstanceTotal] << show() << no_search()
	}

	def show() {
		def interactionInstance = params.messageId ? TextMessage.get(params.messageId.toLong()) : null
		if (interactionInstance && !interactionInstance.read) {
			interactionInstance.read = true
			interactionInstance.save()
		}
		[interactionInstance: interactionInstance]
	}
		
	def contactSearch() {
		render(contentType: 'text/json') { recipientLookupService.lookup(params) }
	}

	private def getSearchDescription(search) {
		String searchDescriptor = message(code: 'searchdescriptor.searching')
		if(search.searchString) {
			searchDescriptor += " \"$search.searchString\""
		} else {
			searchDescriptor += (" " + message(code:'searchdescriptor.all.messages'))
		}
		 
		if(search.group) searchDescriptor += ", $search.group.name"
		if(search.owners) {
			def activity = getActivityInstance()
			String ownerDescription = activity.name
			searchDescriptor += ", $ownerDescription"
		}
		searchDescriptor += message(code:
				search.inArchive? 'searchdescriptor.archived.messages': 'searchdescriptor.exclude.archived.messages')
		if(search.contactString) searchDescriptor += ", $search.contactString"
		search.customFields?.findAll { it.value }?.each { f ->
			searchDescriptor += ", $f.key=$f.value"
		}
		if(search.status) { 
			searchDescriptor += message(code:'searchdescriptor.only', args:[search.status])
		}
		if(search.startDate && search.endDate){
			searchDescriptor += message(code:'searchdescriptor.between', args:[search.startDate, search.endDate])
		} else if(search.startDate) {
			searchDescriptor += message(code:'searchdescriptor.from', args:[search.startDate])
		} else if(search.endDate) {
			searchDescriptor += message(code:'searchdescriptor.until', args:[search.endDate])
		}
		return searchDescriptor
	}

	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'activity'? Activity: MessageOwner
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
	
	private def withSearch(Closure c) {
		def search = Search.get(params.searchId)
		if(search) {
			params.searchString = search.searchString
			params.contactString = search.contactString
			params.groupId = search.group
			params.messageStatus = search.status
			params.activityId = search.activityId
			params.inArchive = search.inArchive
			params.startDate = search.startDate
			params.endDate = search.endDate
			search.customFields.each() { customFieldName, val ->
				params[customFieldName] = val
			}
			params.starred = search.starredOnly
		} else {
			search = new Search(name: 'TempSearchObject')
		}
		c.call(search)
	}

	def recipientCount() {
		def recipients = [params.recipients].flatten()
		def addresses = recipientLookupService.getAddressesFromRecipientList(recipients)
		render([recipientCount: addresses.size()] as JSON)
	}
}

