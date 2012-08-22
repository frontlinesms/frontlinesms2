package frontlinesms2

import grails.converters.JSON

class SubscriptionController extends ActivityController {

	def create = {
		def groupList = Group.getAll()
		[contactList: Contact.list(),
				groupList:groupList]
	}

	def edit = {
		withActivity { activityInstance ->
			def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
			def activityType = activityInstance.shortName
			render view:"../$activityType/create", model:[contactList: Contact.list(),
				groupList:groupList,
				activityInstanceToEdit: activityInstance]
		}
	}

	def join = {
		withSubscription { subscriptionInstance ->
			getCheckedMessages().each { message ->
				subscriptionInstance.processJoin(message)
				println "ownerDetail before subscription save: $message.ownerDetail"
			}
			subscriptionInstance.save(failOnError:true)
		}
		redirect(controller:'message', action:'inbox')
	}

	def leave = {
		withSubscription { subscriptionInstance ->
			getCheckedMessages().each { message ->
				subscriptionInstance.processLeave(message, Contact.findByMobile(message.src))
				subscriptionInstance.addToMessages(message)
			}
			subscriptionInstance.save(failOnError:true)
		}
		redirect(controller:'message', action:'inbox')
	}

	def save = {
		println "**PARAMS** "+params
		withSubscription { subscriptionInstance ->
			subscriptionInstance.group = Group.get(params.subscriptionGroup)
			if(subscriptionInstance.keyword)
				subscriptionInstance.keyword.value = params.keyword.toUpperCase()
			else
				subscriptionInstance.keyword = new Keyword(value: params.keyword.toUpperCase())
			subscriptionInstance.joinAliases = params.joinAliases.toUpperCase()
			subscriptionInstance.leaveAliases = params.leaveAliases.toUpperCase()
			subscriptionInstance.defaultAction = Subscription.Action."${params.defaultAction.toUpperCase()}"
			subscriptionInstance.joinAutoreplyText = params.joinAutoreplyText
			subscriptionInstance.leaveAutoreplyText = params.leaveAutoreplyText
			subscriptionInstance.name = params.name

			if (subscriptionInstance.save(flush:true)) {
				params.activityId = subscriptionInstance.id
				withFormat {
					json { render([ok:true, ownerId: subscriptionInstance.id] as JSON)}
					html { [ownerId:poll.id]}
				}
			}
			else {
				renderJsonErrors(subscriptionInstance)
			}
		}
	}

	def categoriseSubscriptionPopup = {
		render view:"categoriseSubscription", model:[params]
	}

	private def renderJsonErrors(subscription) {
		println "Error:: ${subscription.errors.allErrors}"
		def errorMessages = subscription.errors.allErrors.collect { message(error:it) }.join("\n")
		withFormat {
			json {
				render([ok:false, text:errorMessages] as JSON)
			}
		}
	}

	private def withSubscription(Closure c) {
		def subscriptionInstance = Subscription.get(params.ownerId) ?: new Subscription()
		if (subscriptionInstance) c subscriptionInstance
	}

	private def getCheckedMessages() {
		return Fmessage.getAll(getCheckedMessageList()) - null
	}

	private def getCheckedMessageList() {
		def checked = params.messagesList?: params.messageId?: []
		if(checked instanceof String) checked = checked.split(/\D+/) - ''
		if(checked instanceof Number) checked = [checked]
		if(checked.class.isArray()) checked = checked as List
		return checked
	}
}
