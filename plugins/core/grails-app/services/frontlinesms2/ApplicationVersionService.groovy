package frontlinesms2

class ApplicationVersionService {
	def grailsApplication
	
	def getNewest(String...versions) {
		def w = versions*.tokenize('.')
		def maxLen = w*.size().max()

		for(i in 0..<maxLen) {
			def curMax = w*.getAt(i).max()
			w = w.grep { it[i] == curMax }
		}

		w[0].join('.')
	}
	
	def shouldUpgrade(String alternativeVersion) {
		def currentVersion = grailsApplication.metadata.'app.version'
		currentVersion != getNewest(alternativeVersion, currentVersion)
	}
}
