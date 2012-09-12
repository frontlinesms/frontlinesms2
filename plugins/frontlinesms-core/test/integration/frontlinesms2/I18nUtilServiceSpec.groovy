package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class I18nUtilServiceSpec extends IntegrationSpec {
	def i18nUtilService

	def 'get a list of available translations'() {
		expect:
			i18nUtilService.getAllTranslations() == ['':'English', sw:'Kiswahili']
	}

	@Unroll
	def 'get the locale key from a translation filename'() {
		expect:
			i18nUtilService.getLocaleKey(filename) == localeKey
		where:
			filename                    | localeKey
			'messages.properties'       | ''
			'messages_sw.properties'    | 'sw'
			'messages_pt_BR.properties' | 'pt_BR'
			'messages_fr.properties'    | 'fr'
			'messages_en_US.properties' | 'en_US'
	}

	@Unroll
	def 'get the language name from a messages file'() {
		expect:
			languageName == i18nUtilService.getLanguageName(filename)
		where:
			filename                 | languageName
			'messages.properties'    | 'English'
			'messages_sw.properties' | 'Kiswahili'
	}
}
