package frontlinesms2

class ApiController {
	def grailsApplication
	def index() {
		println "entityClassApiUrl = $params.entityClassApiUrl"
		println "entityId = $params.entityId"
		println "secret = $params.secret"
		println "params = $params"
		def entityClass = grailsApplication.domainClasses*.clazz.find {
			FrontlineApi.isAssignableFrom(it) && it.apiUrl == params.entityClassApiUrl
		}
		def entity = entityClass?.findByIdAndSecret(params.entityId, params.secret)

		if(entity) entity.apiProcess(this)
		else render text:"no access"
	}
}

