package frontlinesms2.api

/**
 * Helper utils for version 1 of the FrontlineSMS api
 */
class FrontlineApi1Utils {
	static final String apiUrlMapping = '/api/1/$entityClassApiUrl/$entityId/' // without secret

	static String generateUrl(FrontlineApi domainInstance) {
		def url = ""
		if(domainInstance.apiEnabled)//domainInstance.isApiEnabled())
			url = apiUrlMapping.replace('$entityClassApiUrl', domainInstance.getClass().apiUrl)
			url = url.replace('$entityId', "${domainInstance.id}")
		return url
	}
}

