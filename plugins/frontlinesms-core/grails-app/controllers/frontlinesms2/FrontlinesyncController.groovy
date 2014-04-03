package frontlinesms2

import grails.converters.JSON

class FrontlinesyncController extends ControllerUtils {
	def frontlinesyncService
	def index() { redirect action:'update' }

	def update() {
		withFconnection { frontlinesyncInstance ->
			frontlinesyncService.updateSyncConfig(params, frontlinesyncInstance, false)
			render ([success:true] as JSON)
		}
	}

	private def withFconnection = withDomainObject Fconnection, { params.id }, { redirect(controller:'connection', action:'list') }
}

