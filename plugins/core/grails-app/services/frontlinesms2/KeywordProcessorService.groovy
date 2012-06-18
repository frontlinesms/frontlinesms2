package frontlinesms2

class KeywordProcessorService {
	def process(Fmessage message) {
		def words = message.text?.trim().toUpperCase().split(/\s/)
		def directMatch = Keyword.getMatch(words[0])
		if(directMatch) {
			directMatch.activity?.processKeyword(message, true)
		} else {
			def indirectMatch
			if(words[0].size() > 1) {
				indirectMatch = Keyword.getMatch(words[0][0..-2])
			}
			indirectMatch = indirectMatch?: Keyword.getMatch('')
			indirectMatch?.activity?.processKeyword(message, false)
		}
	}
}

