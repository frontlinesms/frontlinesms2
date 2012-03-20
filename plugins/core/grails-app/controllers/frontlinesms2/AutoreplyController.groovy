package frontlinesms2

class AutoreplyController extends ActivityController {

	def save = {
		def keyword = new Keyword(value: params.keyword)
		def autoreplyInstance = new Autoreply(name: params.name, sentMessageText: params.autoreplyText, keyword: keyword)
		autoreplyInstance.save(failOnError: true)
		flash.message = "Autoreply has been saved!"
		[ownerId: autoreplyInstance.id]
	}
	
	def sendReply = {
	
	}
	
	def edit = {
		def autoreplyInstance = Autoreply.get(params.ownerId)
		def keyword = autoreplyInstance.keyword
		keyword.value = params.keyword
		autoreplyInstance.name = params.name
		autoreplyInstance.sentMessageText = params.autoreplyText
		autoreplyInstance.save(failOnError: true)
		flash.message = "Autoreply edits have been saved!"
		[ownerId: autoreplyInstance.id]
	}
}
