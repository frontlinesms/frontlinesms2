package frontlinesms2

import frontlinesms2.*

class AutoreplyService {

    def saveInstance(Autoreply autoreply, params) {
		autoreply.name = params.name ?: autoreply.name
		autoreply.autoreplyText = params.messageText ?: autoreply.autoreplyText
		def keywordRawValues = (params.blankKeyword ? '' : params.keywords.toUpperCase()).replaceAll(/\s/, "").split(",")
		autoreply.keywords?.clear()
		autoreply.save(flush:true, failOnError:true)
		for(keywordValue in keywordRawValues) {
			def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
			autoreply.addToKeywords(keyword)
		}
		autoreply.save(failOnError:true,flush:true)
		return autoreply
	}
}
