package frontlinesms2

import grails.converters.JSON

class InlineEditableController extends ControllerUtils {
	def grailsApplication

	def update() {
		println params
		def domainInstance = grailsApplication.getArtefact("Domain", params.domainclass)?.getClazz()?.get(params.instanceid)
		domainInstance?."${params.field}" = params.value
		if(domainInstance?.save()) {
			render ([success:true, value: params.value] as JSON)
		}
		else {
			render ([success:false, error: (domainInstance? domainInstance.errors.allErrors.collect { message(error:it) }.join(" ") : message(code:'domain.not.found'))] as JSON)
		}
	}
}
