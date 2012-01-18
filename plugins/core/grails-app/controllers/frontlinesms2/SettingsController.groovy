package frontlinesms2

import groovy.lang.Closure;

class SettingsController {
	def index = {
		redirect(action:'connections')
	}
	
	def connections = {
		def fconnectionInstanceList = Fconnection.list(params)
		if(!params.id) {
			params.id = Fconnection.list(params)[0]?.id
		}		
		def connectionInstance = Fconnection.get(params.id)
		def fconnectionInstanceTotal = Fconnection.count()
		if(params.id){
			render(view:'show_connections', model:show_connections() << [connectionInstanceList: fconnectionInstanceList,
				connectionInstance: connectionInstance,
				fconnectionInstanceTotal: fconnectionInstanceTotal])
		} else {
			render(view:'show_connections', model: [fconnectionInstanceTotal: 0])
		}
	}
	
	def show_connections = {
		withFconnection {
			[connectionInstance: it] << [settingsSection:'connections',
												connectionInstanceList: Fconnection.list(params),
												fconnectionInstanceTotal: Fconnection.list(params)]
		}
	}
	
	private def withFconnection(Closure c) {
		def connection = Fconnection.get(params.id)
		if(connection) {
			c connection
		} else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
			render(view:'show_connections', model: [fconnectionInstanceTotal: 0])
		}
	}
}
