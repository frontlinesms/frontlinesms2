package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import grails.util.GrailsConfig


class SearchController {
	
	def index = { redirect(action:'no_search') }
	
	def no_search = {
		[groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				messageSection: 'result',
				customFieldList : CustomField.getAllUniquelyNamed()]
	}

	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		true
	}
	
	def result = {
		def search = withSearch { searchInstance ->
			def activity =  getActivityInstance()
			searchInstance.owners = activity ? Fmessage.getMessageOwners(activity): null
			searchInstance.searchString = params.searchString?: ""
			searchInstance.contactString = params.contactString?: null
			searchInstance.group = params.groupId ? Group.get(params.groupId) : null
			searchInstance.status = params.messageStatus ?: null
			searchInstance.activityId = params.activityId ?: null
			searchInstance.activity =  getActivityInstance()
			searchInstance.inArchive = params.inArchive ? true : false
			searchInstance.startDate = params.startDate?:null
			searchInstance.endDate = params.endDate?:null
			//Assumed that we only pass the customFields that exist
			searchInstance.usedCustomField = [:]
			CustomField.getAllUniquelyNamed().each() {
				searchInstance.usedCustomField[it] = params[it+'CustomField']?:""
			} 
			//FIXME easy i discover groovy, so my usage of collection is not good
			//FIXME hard we should rather do a Join but very few documentation available
			searchInstance.customFieldContactList = []
			if (searchInstance.usedCustomField.find{it.value!=''}) {
				def firstLoop = true
				searchInstance.usedCustomField.findAll{it.value!=''}.each { name, value ->
					println("we are looping on "+name+" = "+value)
					if (firstLoop) {
						searchInstance.customFieldContactList = CustomField.findAllByNameLikeAndValueIlike(name,"%"+value+"%")*.contact.name
						firstLoop = false
					} else {
						searchInstance.customFieldContactList.intersect(CustomField.findAllByNameLikeAndValueIlike(name,"%"+value+"%")*.contact.name)
					}
				}
				searchInstance.println("List of contact that match "+search.customFieldContactList)
			}
			searchInstance.save(failOnError: true, flush: true)
		}
		
		def rawSearchResults = Fmessage.search(search)
		def searchResults = rawSearchResults.list(sort:"dateReceived", order:"desc", max: params.max, offset: params.offset)
		def searchDescription = getSearchDescription(search)
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		[searchDescription: searchDescription,
				search: search,
				checkedMessageCount: checkedMessageCount,
				messageInstanceList: searchResults,
				messageInstanceTotal: rawSearchResults.count()] << 
			show(searchResults) << no_search()
	}

	def show = { searchResults ->
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) :searchResults[0]
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		[messageInstance: messageInstance]
	}
		
	private def getSearchDescription(search) {
		String searchDescriptor = "Searching"
		if(search.searchString) {
			searchDescriptor += ' "'+search.searchString+'"'
		} else {
			searchDescriptor += ' all messages'
		}
		 
		if(search.group) searchDescriptor += ", in "+search.group.name
		if(search.owners) {
			def activity = getActivityInstance()
			String ownerDescription = activity instanceof Poll ? activity.title: activity.name
			searchDescriptor += ", in"+ownerDescription
		}
		searchDescriptor += search.inArchive? ", include archived messages":", without archived messages" 
		if(search.contactString) searchDescriptor += ", with contact name="+search.contactString
		if (search.usedCustomField.find{it.value!=''}) {
			search.usedCustomField.find{it.value!=''}.each{
				searchDescriptor += ", with contact having "+it.key+"="+it.value
			}
		}
		if(search.startDate && search.endDate){
			search.startDate.format('dd-MM-yyyy')
			search.endDate.format('dd-MM-yyyy')
			searchDescriptor += ", between "+search.startDate.dateString+" and "+search.endDate.dateString
		}
		return searchDescriptor
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll : Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
	
	private def withFmessage(Closure c) {
		def m = Fmessage.get(params.messageId)
		if(m) c.call(m)
		else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
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
			c.call(search)
		}
		else {
			search = new Search(name: "TempSearchObject")
			c.call(search)
		}
	}
}
