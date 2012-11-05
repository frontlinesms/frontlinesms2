package frontlinesms2

class KeywordProcessorService {
	def process(Fmessage message) {
		println "#vaneyck# ${message.id} KeywordProcessorService.process() "+(new Date().toString())
		def words = message.text?.trim().toUpperCase().split(/\s/)
		def topLevelMatch = Keyword.getFirstLevelMatch(words[0])
		if(!topLevelMatch) topLevelMatch = Keyword.getFirstLevelMatch('')
		if(topLevelMatch) {
			def secondLevelMatch = null
			if(words.length > 1)
				secondLevelMatch = Keyword.getSecondLevelMatchInActivity(words[1], topLevelMatch.activity)
			if (secondLevelMatch) {
				secondLevelMatch.activity.processKeyword(message, secondLevelMatch)
			}
			else
				topLevelMatch.activity.processKeyword(message, topLevelMatch)
		}
	}
}

