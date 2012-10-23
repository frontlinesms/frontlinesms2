package frontlinesms2

import grails.converters.JSON

class SubscriptionController extends ActivityController {
	def subscriptionService

	def create() {
		def groupList = Group.getAll()
		[contactList: Contact.list(),
				groupList:groupList]
	}

	def edit() {
		withActivity { activityInstance ->
			def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
			def activityType = activityInstance.shortName
			render view:"../$activityType/create", model:[contactList: Contact.list(),
				groupList:groupList,
				activityInstanceToEdit: activityInstance]
		}
	}

	def join() {
		withSubscription { subscriptionInstance ->
			getCheckedMessages().each { message ->
				subscriptionInstance.processJoin(message)
			}
			subscriptionInstance.save(failOnError:true)
		}
		redirect(controller:'message', action:'inbox')
	}

	def leave() {
		withSubscription { subscriptionInstance ->
			getCheckedMessages().each { message ->
				subscriptionInstance.processLeave(message)
			}
			subscriptionInstance.save(failOnError:true)
		}
		redirect(controller:'message', action:'inbox')
	}

	def toggle() {
		withSubscription { subscriptionInstance ->
			getCheckedMessages().each { message ->
				subscriptionInstance.processToggle(message)
			}
			subscriptionInstance.save(failOnError:true)
		}
		redirect(controller:'message', action:'inbox')
	}

	def save() {
		def subscriptionInstance = (params.ownerId) ? Subscription.get(params.ownerId) : new Subscription()
		try {
			subscriptionService.saveInstance(subscriptionInstance, params)
			params.activityId = subscriptionInstance.id
			withFormat {
				json { render([ok:true, ownerId: subscriptionInstance.id] as JSON)}
				html { [ownerId:poll.id]}
			}
		}
		catch (Exception e) {
			renderJsonErrors(subscriptionInstance)
		}
	}

	def categoriseSubscriptionPopup() {
		render view:"categoriseSubscription", model:[params]
	}

	private def renderJsonErrors(subscription) {
		println "Error:: ${subscription.errors.allErrors}"
		def collidingKeywords = getCollidingKeywords(params.topLevelKeywords)
		def errorMessages
		if (collidingKeywords)
			errorMessages = collidingKeywords.collect { message(code:'activity.generic.keyword.in.use', args: [it.key, it.value]) }.join("\n")
		else
			errorMessages = subscription.errors.allErrors.collect { message(error:it) }.join("\n")
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
