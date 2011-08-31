package frontlinesms2

import grails.plugin.spock.ControllerSpec

class SearchControllerUnitSpec extends ControllerSpec {
	def "should set max and offset parameters if not sent"() {
		setup:
			registerMetaClass(Fmessage)
			def searchResults = [new Fmessage()]
			Fmessage.metaClass.static.search = {String searchString=null, String contactSearchString=null, Group groupInstance=null, Collection<MessageOwner> messageOwner=[], max, offset ->
				assert max == 10
				assert offset == 0
				searchResults
			}
			mockDomain(Fmessage)
			mockDomain(Contact)
			mockDomain(Group)
			mockDomain(Poll)
			mockDomain(Folder)
			mockParams.max = 10
		when:
			def results = controller.result()
		then:
			results.messageInstanceList == searchResults		
	}

	def "should send the max and offset parameters to db if sent"() {
		setup:
			registerMetaClass(Fmessage)
			def searchResults = [new Fmessage()]
			Fmessage.metaClass.'static'.search = {String searchString=null,  String contactSearchString=null, Group groupInstance=null, Collection<MessageOwner> messageOwner=[], max, offset ->
				assert max == 5
				assert offset == 7
				searchResults
			}
			mockDomain(Fmessage)
			mockDomain(Contact)
			mockDomain(Group)
			mockDomain(Poll)
			mockDomain(Folder)
			mockParams.max = 5
		    mockParams.offset = 7
		when:
			def results = controller.result()
		then:
			results.messageInstanceList == searchResults
	}
}