package frontlinesms2.services

import frontlinesms2.*
import grails.plugin.spock.*

class FmessageInfoServiceSpec extends UnitSpec {
	def fmessageInfoService
	
	def setup() {
		fmessageInfoService = new FmessageInfoService()
	}
	
	def "should calculate the total number of characters and messages being sent"() {
		when:
			def messageInfo = fmessageInfoService.getMessageInfos('')
		then:
			messageInfo.charCount == 0
			messageInfo.remaining == 160
			messageInfo.partCount == 0
		when:
			messageInfo = fmessageInfoService.getMessageInfos("a" * 160)
		then:
			messageInfo.charCount == 160
			messageInfo.remaining == 0
			messageInfo.partCount == 1
		when:
			messageInfo = fmessageInfoService.getMessageInfos("a" * 161)
		then:
			messageInfo.charCount == 161
			messageInfo.remaining == 109
			messageInfo.partCount == 2
		when:
			messageInfo = fmessageInfoService.getMessageInfos("a" * 270)
		then:
			messageInfo.charCount == 270
			messageInfo.remaining == 0
			messageInfo.partCount == 2
	}
}
