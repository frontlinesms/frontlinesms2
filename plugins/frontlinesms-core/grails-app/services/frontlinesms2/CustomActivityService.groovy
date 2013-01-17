package frontlinesms2

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class CustomActivityService {
	static transactional = true


	def saveInstance(customActivity, params) {
		println "customActivity Params ::${params}"
		def steps = new JSONArray(params.jsonToSubmit)
		customActivity.name = params.name
		//TODO DRY the functionality of creating and editing keywords
		customActivity.keywords?.clear()
		println "removing existing steps if any"
		customActivity.steps?.clear()
		println "##Just about to save"
		customActivity.save(flush:true, failOnError:true)
		println "##Just saved round 1"
		
		getSteps(steps).each {
			customActivity.addToSteps(it)
		}

		if(params.sorting == 'global'){
			customActivity.addToKeywords(new Keyword(value:''))
		} else if(params.sorting == 'enabled'){
			def keywordRawValues = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(",")
			for(keywordValue in keywordRawValues) {
				def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
				customActivity.addToKeywords(keyword)
			}
		} else {
			println "##### CustomActivityService.saveInstance() # removing keywords"
		}

		println "# 2 ######### Saving Round 2 # $customActivity.errors.allErrors"
		customActivity.save(failOnError:true,flush:true)
	}

	private getSteps(steps) {
		def stepInstanceList = []
		println "steps::::: $steps"

		steps.each { step ->
			println "step:::: $step"
			
			def stepInstance = Step.implementations.find {it.shortName == step.stepType}.newInstance(step)
			if(step.stepProperty instanceof JSONObject) {
				stepInstance.addToStepProperties(new StepProperty(key:step.stepProperty.key, value:step.stepProperty.value))
			} else {
				step.stepProperty.each { stepProperty ->
					stepInstance.addToStepProperties(new StepProperty(key:stepProperty.key, value:stepProperty.value))
				}
			}

			stepInstanceList << stepInstance
		}
		stepInstanceList.each {
			println "<<<>>>"
			println "$it"
			println "${it.stepProperties*.key}"
			println "<<<>>>"
		}
		stepInstanceList
	}

	def triggerSteps(activity, message) {
		println "activity::: ${activity}"
		println "activity steps::: ${activity.steps}"
		activity.steps.each {
			it.doAction(message)
		}
	}
}