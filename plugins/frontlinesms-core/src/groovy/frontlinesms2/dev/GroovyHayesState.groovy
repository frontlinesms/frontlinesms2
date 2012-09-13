package frontlinesms2.dev

import java.util.regex.Pattern

class GroovyHayesState {
	def error
	def responses
	def extraProperties
	
	def GroovyHayesState(Map args) {
		error = args.error
		responses = args.responses
		extraProperties = args
	}
  
	def setResponse(request, response) {
		responses << request << response
	}
	
	def getResponse(String request) {
		println "GroovyHayesState.getResponse() : request=$request"
		try {
			for(int i=0; i<responses.size; i+=2) {
				def mapRequest = responses[i]
				if((mapRequest instanceof String && mapRequest==request) ||
						mapRequest instanceof Pattern && mapRequest.matcher(request).matches()) {
					def response = responses[i+1]
					println "GroovyHayesState.getResponse() : For request $request, returning: ${response.toString()}"
					return responses[i+1]
				}
			}
		} catch(Exception ex) { ex.printStackTrace() }
		println "GroovyHayesState.getResponse() : For request $request, returning: $error"
		return error
	}
	
	def propertyMissing(String name) {
		extraProperties."${name}"
	}
}
