package frontlinesms2

import frontlinesms2.api.*

class ApiController extends ControllerUtils {
	def grailsApplication
	def index() {
		println "entityClassApiUrl = $params?.entityClassApiUrl"
		println "entityId = $params?.entityId"
		println "params = $params"
		def entityClass = grailsApplication.domainClasses*.clazz.find {
			FrontlineApi.isAssignableFrom(it) && (it.getAnnotation(FrontlineApiAnnotations.class)?.apiUrl() == params.entityClassApiUrl)
		}
		def entity = entityClass?.findById(params.entityId)

		if(entity) {
			entity.apiProcess(this)
		} else {
			render text:'not found', status:404
		}
	}
}

