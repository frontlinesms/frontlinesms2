package frontlinesms2

class KeywordProcessorService {
	private static final ALPHABET = "abcdefghijklmnopqrstuvwxyz"
	static transactional = true

	boolean matches(String messageText) {
		getPollResponse(messageText) != null
	}

	PollResponse getPollResponse(String messageText) {
		def words = messageText.split()
		if(words.size() == 0) {
			return null
		} else if(words.size() == 1) {
			def word = words[0]
			def keyword = word[0..-2]
			def option = word[-1]
			return getPollResponse(keyword, option)
		} else {
			def keyword = words[0]
			def option = words[1]
			def pollResponse = getPollResponse(keyword, option)
			if(pollResponse) return pollResponse
			else return getPollResponse(keyword) // TODO should do this in a single select
		}
		return null
	}
	
	PollResponse getPollResponse(String keyword, String option) {
		if(option.size() != 1 || keyword.size() < 1) {
			return null
		} else {
			return Poll.findByTitleILike(keyword)?.responses?.getAt(getPollResponseIndex(option))
		}
	}
	
	int getPollResponseIndex(String option) {
		assert option.size() == 1
		return ALPHABET.indexOf(option.toLowerCase())
	}
}
