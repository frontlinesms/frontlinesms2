package frontlinesms2

class SecurityFilters {
	def grailsApplication

	def filters = {
		basicAuth(controller:'*', action:'*') {
			before = {
				def enabledAuthentication = grailsApplication.config.frontlinesms.enabledAuthentication
				println "### enabledAuthentication: $enabledAuthentication"
				if(enabledAuthentication) {
					def basicAuthRequired = { credentialsProvided ->
							if(credentialsProvided) response.status = 403
								else {
										response.status = 401
										response.addHeader('WWW-Authenticate', 'Basic realm="FrontlineSMS"')
								}
								render text:'Restricted Page';
								return false
						}
					def username = new String(grailsApplication.config.frontlinesms.username.decodeBase64())
					def password = new String(grailsApplication.config.frontlinesms.password.decodeBase64())

					def authString = request.getHeader('Authorization')
					if(!authString) return basicAuthRequired()
							authString -= 'Basic '
							def decoded = new String(authString.decodeBase64())
							if(decoded != "$username:$password") return basicAuthRequired()
									return true
				}
			}
	
		}
	}
}
