package frontlinesms2.dev

import net.frontlinesms.test.serial.hayes.*

class GroovyHayesPortHandler extends BaseHayesPortHandler {
	GroovyHayesState currentState
	
	public GroovyHayesPortHandler(GroovyHayesState initialState) {
		currentState = initialState
	}
	
	def propertyMissing(String name) {
		currentState."$name"
	}
	
	@Override
	protected String getResponseText(String request) {
		println "Getting response for $request"
		def response = currentState.getResponse(request)
		if(response instanceof String) {
			println "Got response as String"
			return response
		} else {
			println "Calling closure with ($this, $request) as arguments"
			return response.call(this, request)
		}
	}
}