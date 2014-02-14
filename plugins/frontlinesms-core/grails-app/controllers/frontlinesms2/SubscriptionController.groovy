package frontlinesms2

class SubscriptionController extends ActivityController {
	def subscriptionService

	def create() {
	}

	def edit() {
		withActivity { activityInstance ->
			def activityType = activityInstance.shortName
			render view:"../$activityType/create", model: [
				activityInstanceToEdit:activityInstance
			]
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
		//TODO Should use the withDefault subscription closure
		def subscriptionInstance = Subscription.get(params.ownerId)?: new Subscription()
		params.keywords = (params.topLevelKeywords?.trim()?.length() > 0) ? params.topLevelKeywords:("${params.joinKeywords},${params.leaveKeywords}")
		doSave(subscriptionService, subscriptionInstance)
	}

	def categoriseSubscriptionPopup() {
		render view:"categoriseSubscription", model:[params]
	}

	private def withSubscription(Closure c) {
		def subscriptionInstance = Subscription.get(params.ownerId) ?: new Subscription()
		if (subscriptionInstance) c subscriptionInstance
	}

	private def getCheckedMessages() {
		return TextMessage.getAll(getCheckedMessageList()) - null
	}

	private def getCheckedMessageList() {
		def checked = params.messagesList?: params.messageId?: []
		if(checked instanceof String) checked = checked.split(/\D+/) - ''
		if(checked instanceof Number) checked = [checked]
		if(checked.class.isArray()) checked = checked as List
		return checked
	}
}

