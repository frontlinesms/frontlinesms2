package frontlinesms2

import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.support.RequestContextUtils
import org.springframework.util.StringUtils

class I18nUtilService {
	def servletContext
	def messageSource
	def appSettingsService

	private allTranslations

	def getCurrentLanguage(def request) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request)
		return localeResolver.resolveLocale(request).toString()
	}

	def setLocale(request, response, language) {
		Locale locale = StringUtils.parseLocaleString(language)
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request)
		localeResolver.setLocale(request, response, locale)
		appSettingsService.set('language', language)
	}

	synchronized def getAllTranslations() {
		if(!this.@allTranslations) {
			def translations = [:]
			new  File(getRootDirectory()).eachFileMatch groovy.io.FileType.FILES, ~/messages(_\w\w)*\.properties$/, { file ->
				def filename = file.name
				def locale = getLocaleKey(filename)
				def language = getLanguageName(filename)
				translations[locale] = language
			}
			this.allTranslations = translations.sort { it.value }
		}
		return this.@allTranslations
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
		def fileURL = new File('web-app/WEB-INF/grails-app/i18n').path
	}
}

