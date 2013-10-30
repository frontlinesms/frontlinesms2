package frontlinesms2

import com.google.i18n.phonenumbers.*

class MobileNumberUtilService {
	def getISOCountryCode(rawNumber) {
		def phoneNumberUtil = PhoneNumberUtil.getInstance()
		def number

		try {
			number = phoneNumberUtil.findNumbers(rawNumber, null).iterator().next().number()
		} catch (NoSuchElementException exception) {
			return ''
		}

		CountryCodeToRegionCodeMap.countryCodeToRegionCodeMap[number.countryCode]
	}
}
