package frontlinesms2

import grails.converters.JSON

class FrontlinesyncController extends ControllerUtils {
	def index() { redirect action:'update' }

	def update() {
		withFconnection { frontlinesyncInstance ->
			frontlinesyncInstance.properties = params
			frontlinesyncInstance.configSynced =  false
			frontlinesyncInstance.save()
			render ([success:true] as JSON)
		}
	}

	private def withFconnection = withDomainObject Fconnection, { params.id }, { redirect(controller:'connection', action:'list') }
}

