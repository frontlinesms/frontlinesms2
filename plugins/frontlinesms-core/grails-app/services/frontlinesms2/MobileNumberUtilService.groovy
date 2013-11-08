package frontlinesms2

import com.google.i18n.phonenumbers.*

class MobileNumberUtilService {
	def getISOCountryCode(rawNumber) {
		def phoneNumberUtil = PhoneNumberUtil.instance
		def number

		try {
			number = phoneNumberUtil.parse(rawNumber, null)
		} catch (NoSuchElementException exception) {
			return ''
		} catch (NumberParseException exception) {
			return ''
		}

		phoneNumberUtil.getRegionCodeForNumber(number)
	}

	def getFlagCSSClasses(phoneNumber, allowEmpty=true) {
		def isoCode
		def flagCssClass = ''
		isoCode = getISOCountryCode(phoneNumber)?.toLowerCase()
		if(isoCode) flagCssClass = "flag flag-$isoCode"
		else if(!allowEmpty) flagCssClass = 'flag'
		flagCssClass
	}
}
