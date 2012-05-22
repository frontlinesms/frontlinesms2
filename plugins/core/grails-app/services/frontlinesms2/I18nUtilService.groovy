package frontlinesms2

class I18nUtilService {
	def servletContext
	def messageSource

	def getAllTranslations() {
		def allTranslations = [:]
		new  File(getRootDirectory()).eachFileMatch groovy.io.FileType.FILES, ~/messages(_\w\w)*\.properties$/, { file ->
			def filename = file.name
			def locale = getLocaleKey(filename)
			def language = getLanguageName(filename)
			allTranslations[locale] = language
		}
		return allTranslations.sort { it.value }
	}

	def getLocaleKey(filename) {
		filename - 'properties' - 'messages' - '_' - '.'
	}

	def getLanguageName(filename) {
		def f = new File(getRootDirectory(), filename)
		if(f.exists()) {
			def lang
			try { f.eachLine { line ->
				if(line.startsWith("language.name=")) {
					lang = line - "language.name="
					throw new EOFException()
				}
			} } catch(EOFException _) {}
			return lang
		}
	}

	def getMessage(args) {
		// maybe we need Locale.setDefault(new Locale("en","US"))
		def text = messageSource.getMessage(args.code, args.args as Object[], null)
	}

	private String getRootDirectory() {
		def fileURL = new File('webapp/WEB-INF/grails-app/i18n').path
	}
}

