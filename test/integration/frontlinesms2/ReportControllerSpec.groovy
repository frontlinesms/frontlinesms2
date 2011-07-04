package frontlinesms2


class ReportControllerSpec extends grails.plugin.spock.IntegrationSpec {
	
	def controller
	def setup() {
		controller = new ReportController()
		[new Fmessage(src:'gerad', text:'i love pie'),
			new Fmessage(src:'steve', text:'i hate beef')].each() {
					it.save(failOnError:true)
				}
	}
	
	def cleanup() {
		Fmessage.findAll()*.delete(flush:true, failOnError:true)
	}
	
	def "csv file is generated from provided list of messages"() {
		when:
			def model = controller.generateCSVReport("test" ,[Fmessage.findBySrc('gerad'), Fmessage.findBySrc('steve')]).messageInstanceList
		then:
			model == [Fmessage.findBySrc('gerad'), Fmessage.findBySrc('steve')]
	}
	
	def "pdf file is generated from provided list of messages"() {
		when:
			def model = controller.generatePDFReport("test",[Fmessage.findBySrc('gerad'), Fmessage.findBySrc('steve')]).messageInstanceList
		then:
			model == [Fmessage.findBySrc('gerad'), Fmessage.findBySrc('steve')]
	}
}

