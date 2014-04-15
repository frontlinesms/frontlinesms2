package frontlinesms2

import frontlinesms2.api.*

class ApiController extends ControllerUtils {
	def grailsApplication
	def apiService

	def index() {
		def entityClass = grailsApplication.domainClasses*.clazz.find {
			FrontlineApi.isAssignableFrom(it) && (it.getAnnotation(FrontlineApiAnnotations.class)?.apiUrl() == params.entityClassApiUrl)
		}
		def entity = entityClass?.findById(params.entityId)

		if(entity) {
			apiService.invokeApiProcess(entity, this)
		} else {
			render text:'not found', status:404
		}
	}
}

