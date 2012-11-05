package frontlinesms2

import grails.converters.JSON
class AutoforwardController extends ActivityController {
	def autoforwardService

	def save() {
		def autoforward
		if(Autoforward.get(params.ownerId))
			autoforward = Autoforward.get(params.ownerId)
		else
			autoforward = new Autoforward()
		try { 
			autoforwardService.saveInstance(autoforward, params)
			flash.message = message(code:'autoforward.saved')
			params.activityId = autoforward.id
			withFormat {
				json { render([ok:true, ownerId:autoforward.id] as JSON) }
				html { [ownerId:autoforward.id] }
			}
		}
		catch (Exception e) {
			//first check if it is due to colliding keywords, so we can generate a more helpful message.
			def collidingKeywords = getCollidingKeywords(params.sorting == 'global'? '' : params.keywords)
			def errors
			if (collidingKeywords)
				errors = collidingKeywords.collect { 
					if(it.key == '')
						message(code:'activity.generic.global.keyword.in.use', args: [it.value])
					else
						message(code:'activity.generic.keyword.in.use', args: [it.key, it.value])
				}.join("\n")
			else
				errors = autoforward.errors.allErrors.collect {message(code:it.codes[0], args: it.arguments.flatten(), defaultMessage: it.defaultMessage)}.join("\n")
			withFormat {
				json { render([ok:false, text:errors] as JSON) }
			}
		}
	}
	
}