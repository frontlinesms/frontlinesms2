package frontlinesms2

class UrlHelperService {
	def i18nUtilService

	String getBaseUrl(request) {
		def scheme = request.scheme.toLowerCase()
		def port = request.serverPort
		def explicitPort = (scheme == 'http' && port != 80) || (scheme == 'https' && port != 443)
		def domain = (request.serverName == 'localhost' ? "&lt;${i18nUtilService.getMessage(code:'localhost.ip.placeholder')}&gt;" : request.serverName)
		return "${scheme}://${domain}${explicitPort ? (':' + port ):''}"
	}
}
