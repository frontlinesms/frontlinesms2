package frontlinesms2

import org.hibernate.criterion.CriteriaSpecification

class FmessageService {
	def messageSendService
	
    def move(messageList, activity, params) {
    	def messagesToSend = []
    	messageList.each { messageInstance ->
    		if(messageInstance.isMoveAllowed()){
    			messageInstance.clearAllDetails()
    			messageInstance.isDeleted = false
				Trash.findByObject(messageInstance)?.delete(failOnError:true)
				if (params.messageSection == 'activity') {
					activity.move(messageInstance)
					activity.save(failOnError:true, flush:true)
				} else if (params.ownerId && params.ownerId != 'inbox') {
					messageInstance.messageOwner?.removeFromMessages(messageInstance)?.save(failOnError:true)
					MessageOwner.get(params.ownerId).addToMessages(messageInstance).save(failOnError:true)
					messageInstance.save()
				} else {
					messageInstance.with {
						if(messageOwner) {
							messageOwner.removeFromMessages(messageInstance).save(failOnError:true)
							save(failOnError:true)
						}
					}
				}
			}
		}

		if(messagesToSend) {
			MessageSendJob.defer(messagesToSend)
		}
    }

    def search(search) {
    	def fmessageInstanceList = fmessageFilter(search)
    	def rawSearchResults = []
    	if(fmessageInstanceList) {
    		rawSearchResults = Fmessage.search(fmessageInstanceList*.id)
    	}
    	rawSearchResults
    }

    private def fmessageFilter(search) {
    	def ids = Fmessage.withCriteria {
			createAlias('dispatches', 'disp', CriteriaSpecification.LEFT_JOIN)
			if(search.searchString) {
				or {
					ilike("text", "%${search.searchString}%")
					ilike("src", "%${search.searchString}%")
					ilike("disp.dst", "%${search.searchString}%")
				}
			}
			if(search.contactString) {
				def contactNumbers = Contact.findAllByNameIlike("%${search.contactString}%")*.mobile ?: ['']
				or {
					'in'('src', contactNumbers)
					'in'('disp.dst', contactNumbers)
				}
			}
			if(search.group) {
				def groupMembersNumbers = search.group.addresses?: [''] //otherwise hibernate fail to search 'in' empty list
				or {
					'in'('src', groupMembersNumbers)
					'in'('disp.dst', groupMembersNumbers)
				}
			}
			if(search.status) {
				if(search.status.toLowerCase() == 'inbound') eq('inbound', true)
				else eq('inbound', false)
			}
			if(search.owners) {
				'in'("messageOwner", search.owners)
			}
			if(search.startDate && search.endDate) {
				between('date', search.startDate, search.endDate)
			} else if (search.startDate) {
				ge('date', search.startDate)
			} else if (search.endDate) {
				le('date', search.endDate)
			}
			if(search.customFields.any { it.value }) {
				// provide empty list otherwise hibernate fails to search 'in' empty list
				def matchingContactsNumbers = Contact.findByCustomFields(search.customFields)*.mobile?: ['']
				or {
					'in'('src', matchingContactsNumbers)
					'in'('disp.dst', matchingContactsNumbers)
				}
			}
			if(!search.inArchive) {
				eq('archived', false)
			}
			if(search.starredOnly) {
				eq('starred', true)
			}
			eq('isDeleted', false)
			// order('date', 'desc') removed due to http://jira.grails.org/browse/GRAILS-8162; please reinstate when possible
		}*.refresh() // TODO this is ugly ugly, but it fixes issues with loading incomplete dispatches.  Feel free to sort it out
    }
}
