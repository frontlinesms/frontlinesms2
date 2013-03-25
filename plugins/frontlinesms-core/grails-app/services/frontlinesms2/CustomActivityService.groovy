package frontlinesms2

import org.codehaus.groovy.grails.web.json.JSONArray

class CustomActivityService {
	/* TODO if you do insist on committing printlns, please make sure they are neat and make sense when
	   reading them on the console.  E.g. include the class and method name at the start, make them precise.
	   Things like the following are not helpful:
			println "<<<>>>"
			println "$it"
	 */
	def saveInstance(customActivity, params) {
		println "customActivity Params ::${params}"
		def steps = new JSONArray(params.jsonToSubmit)
		customActivity.name = params.name
		//TODO DRY the functionality of creating and editing keywords
		customActivity.keywords?.clear()
				
		//Removing Steps
		def storedSteps = customActivity.steps
		println "# StepsIds in # ${steps*.stepId.collect { (it != "")?(it as Long):null }}"
		def stepsToDelete = (storedSteps*.id?:[]) - steps*.stepId.collect { (it != "")?(it as Long):null }
		println "# Steps saved already ${customActivity.steps*.id}"
		println "# Steps to delete ${stepsToDelete}"

		stepsToDelete.each { it->
			customActivity.removeFromSteps(Step.get(it))
		}
		customActivity.save(failOnError:true, flush:true)

		//Adding Steps
		println "# Adding the steps"
		steps.each { step->
			def stepToEdit = customActivity.steps.find { "${it.id}" == step.stepId } ?: Step.implementations.find {it.shortName == step.stepType}.newInstance(step)
			println "# StepToEdit_ID # ${stepToEdit.id}"
			println "# Adding step of type # ${stepToEdit.shortName}"
			println "# Clearing step properties before update #"
			stepToEdit.stepProperties?.clear()
			if(stepToEdit.id) stepToEdit.save(failOnError:true, flush:true)
			step.each { k,v->
				if(!(k in ["stepType", "stepId"])) {
					stepToEdit.setPropertyValue(k,v)
					println "# Setting $k $v for ${step.stepType}"
				}
			}
			if(!stepToEdit.id)
				customActivity.addToSteps(stepToEdit)
			else
				stepToEdit.save()
		}

		println "# The steps to save are ${customActivity.steps*.shortName}"

		println "##Just about to save"
		// FIXME why are there multiple saves here?
		customActivity.save(flush:true, failOnError:true)
		println "##Just saved round 1"

		if(params.sorting == 'global'){
			customActivity.addToKeywords(new Keyword(value:''))
		} else if(params.sorting == 'enabled'){
			def keywordRawValues = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(",")
			for(keywordValue in keywordRawValues) {
				def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
				println "# Adding keyword # ${keyword.value}"
				customActivity.addToKeywords(keyword)
			}
		} else {
			println "##### CustomActivityService.saveInstance() # removing keywords"
		}

		println "# 2 ######### Saving Round 2 # $customActivity.errors.allErrors"
		// FIXME why are there multiple saves here?
		customActivity.save(failOnError:true, flush:true)
	}

	def triggerSteps(c, message) {
		println "c::: ${c}, message::: $message"
		println "c steps::: ${c.steps}"
		c.steps.each {
			println "calling process on $it"
			it.process(message)
		}
	}
}
