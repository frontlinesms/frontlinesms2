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

	def getFlagCSSClasses(phoneNumber, allowEmpty=false) {
		if (!phoneNumber) {
			return ((allowEmpty)?'':'flag flag-frontlinesms')
		}
		else {
			return "flag flag-${getISOCountryCode(phoneNumber)?.toLowerCase() ?: 'frontlinesms'}"
		}
	}
}
