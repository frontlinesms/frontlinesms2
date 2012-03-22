package frontlinesms2

class AutoreplyController extends ActivityController {

	def save = {
		withAutoreply { autoreply ->
			autoreply.keyword ? autoreply.keyword.value = params.keyword : (autoreply['keyword'] = new Keyword(value: params.keyword))
			autoreply.name = params.name ?: autoreply.name
			autoreply.autoreplyText = params.autoreplyText ?: autoreply.autoreplyText
			autoreply.save(flush: true, failOnError: true)
			flash.message = "Autoreply has been saved!"
			[ownerId: autoreply.id]
		}
	}
	
	private def withAutoreply(Closure c) {
		Autoreply.get(params.ownerId) ?: new Autoreply()
	}
}
