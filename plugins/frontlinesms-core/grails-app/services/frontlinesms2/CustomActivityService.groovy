package frontlinesms2

import org.codehaus.groovy.grails.web.json.JSONArray

class CustomActivityService {
	def recipientLookupService
	def saveInstance(customActivity, params) {
		def steps = new JSONArray(params.jsonToSubmit)
		customActivity.name = params.name
		//TODO DRY the functionality of creating and editing keywords
		customActivity.keywords?.clear()
				
		def storedSteps = customActivity.steps
		// FIXME please fix formatting
		def stepsToDelete = (storedSteps*.id?:[]) - steps*.stepId.collect { (it != "")?(it as Long):null }

		// FIXME please add spaces before ->
		stepsToDelete.each { it->
			customActivity.removeFromSteps(Step.get(it))
		}
		customActivity.save(failOnError:true, flush:true)

		// FIXME please add spaces before ->
		steps.each { step->
			// FIXME please fix formatting
			def stepToEdit = customActivity.steps.find { "${it.id}" == step.stepId } ?: Step.implementations.find {it.shortName == step.stepType}.newInstance(step)
			stepToEdit.stepProperties?.clear()
			if(stepToEdit.id) stepToEdit.save(failOnError:true, flush:true)
			// FIXME please add spaces after commas and before ->
			step.each { k,v->
				if(k == "recipients") {
					stepToEdit.setRecipients(recipientLookupService.contactSearchResults([recipients:v]).values() as List)
				} // FIXME please sort out linebreaks after braces
				else if(!(k in ["stepType", "stepId"])) {
					stepToEdit.setPropertyValue(k,v)
				}
			}
			// FIXME please add braces around if/else statements
			if(!stepToEdit.id)
				customActivity.addToSteps(stepToEdit)
			else
				stepToEdit.save()
		}

		// FIXME why are there multiple saves here?
		customActivity.save(flush:true, failOnError:true)

		// FIXME please add spaces before braces
		if(params.sorting == 'global'){
			customActivity.addToKeywords(new Keyword(value:''))
		// FIXME please add spaces before braces
		} else if(params.sorting == 'enabled'){
			def keywordRawValues = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(",")
			// FIXME please use .each() instead of for()
			for(keywordValue in keywordRawValues) {
				def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
				customActivity.addToKeywords(keyword)
			}
		}

		// FIXME why are there multiple saves here?
		customActivity.save(failOnError:true, flush:true)
	}

	def triggerSteps(c, message) {
		c.steps.each {
			it.process(message)
		}
	}
}

