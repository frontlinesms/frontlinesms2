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
		println "GroovyHayesPortHandler.getResponseText() : Getting response for $request"
		def response = currentState.getResponse(request)
		while(true) {
			if(response instanceof String) {
				println "GroovyHayesPortHandler.getResponseText() : Got response as String ($response)"
				return response
			} else if(response instanceof GroovyHayesResponse) {
				println "GroovyHayesPortHandler.getResponseText() : Got response as HayesState ($response.text -> $response.nextState)"
				println "GroovyHayesPortHandler.getResponseText() : Next state responses: $response.nextState.responses"
				currentState = response.nextState
				return response.text
			} else if(response instanceof Exception) {
				throw response;
			} else if(response instanceof Closure) {
				response = response.call(this, request)
			} else throw new RuntimeException("Unrecognised Hayes response: $response")
		}
	}
}
