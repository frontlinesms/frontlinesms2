package frontlinesms2

class KeywordProcessorService {
	def process(Fmessage message) {
		def words = message.text?.trim().toUpperCase().split(/\s/)
		def directMatch = Keyword.findByValue(words[0])
		if(directMatch) {
			directMatch.activity?.processKeyword(message, true)
		} else {
			def indirectMatch
			if(words[0].size() > 1) {
				indirectMatch = Keyword.findByValue(words[0][0..-2])
			}
			indirectMatch = indirectMatch?: Keyword.findByValue('')
			indirectMatch?.activity?.processKeyword(message, false)
		}
	}
}

