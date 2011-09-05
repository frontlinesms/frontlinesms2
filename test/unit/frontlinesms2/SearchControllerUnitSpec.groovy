package frontlinesms2

import grails.plugin.spock.ControllerSpec

class SearchControllerUnitSpec extends ControllerSpec {
	def "should set max and offset parameters if not sent"() {
		setup:
			registerMetaClass(Fmessage)
			def searchResults = [new Fmessage()]
			Fmessage.metaClass.static.search = {params ->
				assert params.max == 10
				assert params.offset == 0
				searchResults
			}
			mockDomain(Fmessage)
			mockDomain(Contact)
			mockDomain(Group)
			mockDomain(Poll)
			mockDomain(Folder)
			mockParams.max = 10
			controller.beforeInterceptor()
		when:
			def results = controller.result()
		then:
			results.messageInstanceList == searchResults		
	}

	def "should send the max and offset parameters to db if sent"() {
		setup:
			registerMetaClass(Fmessage)
			def searchResults = [new Fmessage()]
			Fmessage.metaClass.'static'.search = {params ->
				assert params.max == 5
				assert params.offset == 7
				searchResults
			}
			mockDomain(Fmessage)
			mockDomain(Contact)
			mockDomain(Group)
			mockDomain(Poll)
			mockDomain(Folder)
			mockParams.max = 5
		    mockParams.offset = 7
			controller.beforeInterceptor()
		when:
			def results = controller.result()
		then:
			results.messageInstanceList == searchResults
	}
}
