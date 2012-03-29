package frontlinesms2

class KeywordProcessorService {
	def process(Fmessage message) {
		def words = message.text?.trim().toUpperCase().split(/\s/)
		if(!words) return
		def directMatch = Keyword.findByValue(words[0])
		if(directMatch) directMatch.activity?.processKeyword(message, true)
		else if(words[0].size()>1) {
			Keyword.findByValue(words[0][0..-2])?.activity?.processKeyword(message, false)
		}
	}
	
}

