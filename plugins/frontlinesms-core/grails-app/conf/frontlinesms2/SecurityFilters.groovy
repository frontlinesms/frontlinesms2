package frontlinesms2

class SecurityFilters {
	def appSettingsService

	def filters = {
		basicAuth(controller:'*', action:'*', uriExclude:'/api/**') {
			before = {
				def enabledAuthentication = appSettingsService.get("enabledAuthentication")
				if(enabledAuthentication) {
					def basicAuthRequired = { credentialsProvided ->
						if(credentialsProvided) response.status = 403
						else {
								response.status = 401
								response.addHeader('WWW-Authenticate', 'Basic realm="FrontlineSMS"')
						}
						return false
					}
					def username = new String(appSettingsService.get("username").decodeBase64())
					def password = new String(appSettingsService.get("password").decodeBase64())

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
