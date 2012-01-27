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
		for(int i=0; i<responses.size; i+=2) {
			def mapRequest = responses[i]
			if((mapRequest instanceof String && mapRequest==request) ||
					mapRequest instanceof Pattern && mapRequest.matcher(request).matches()) {
				//def response = responses[i+1]
				//println "For request $request, returning: $response"
				return responses[i+1]
			}
		}
		println "For request $request, returning: $error"
		error
	}
	
	def propertyMissing(String name) {
		extraProperties."${name}"
	}
}