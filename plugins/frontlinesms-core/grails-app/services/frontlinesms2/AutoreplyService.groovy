package frontlinesms2

import frontlinesms2.*

class AutoreplyService {
	def saveInstance(Autoreply autoreply, params) {
		autoreply.name = params.name ?: autoreply.name
		autoreply.autoreplyText = params.messageText ?: autoreply.autoreplyText
		autoreply.keywords?.clear()
		autoreply.save(flush:true, failOnError:true)

		if(params.sorting == 'global') {
			autoreply.addToKeywords(new Keyword(value:''))
		} else if(params.sorting == 'enabled') {
			def keywordRawValues = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(",")
			for(keywordValue in keywordRawValues) {
				def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
				autoreply.addToKeywords(keyword)
			}
		} else {
			println "##### AutoreplyService.saveInstance() # removing keywords"
		}
		autoreply.save(failOnError:true, flush:true)
	}
}

