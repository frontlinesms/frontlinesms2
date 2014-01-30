import spock.lang.*
import grails.plugin.spock.*

import frontlinesms2.*
import frontlinesms2.online.*

class MessageStorageServiceTimingSpec extends IntegrationSpec {
	static final def RANDOM = new Random()
	static final int MESSAGE_COUNT = 60000
	long timeStarted
	def service

	def setup() {
		service = new MessageStorageService()
	}

	@Unroll
	def 'time for a new fmessage'() {
		given:
			def m = TextMessage.buildWithoutSave()
			def c = createFconnection()
			addMessages(c, MESSAGE_COUNT)
			startTimer()
		when:
			service.process(mockExchange(m, c))
		then:
			printTotalTimeTaken()
	}

	def 'time for an existing fmessage'() {
		given:
			def m = TextMessage.build()
			def c = createFconnection()
			addMessages(c, MESSAGE_COUNT)
			startTimer()
		when:
			service.process(mockExchange(m, c))
		then:
			printTotalTimeTaken()
	}

	private def mockExchange(message, connection) {
		[
			in:[
				headers:['fconnection-id':connection.id],
				body:message
			],
			setProperty:{ a, b -> }
		]
	}

	private def createFconnection() {
		SmssyncFconnection.build()
	}

	private def addMessages(connection, messageCount) {
		println "Creating $messageCount messages..."
		messageCount.times {
			TextMessage.build(connectionId:connection.id)
		}
		println "Messages created."
	}

	private def startTimer() {
		megaMessage("STARTING TIMER")
		timeStarted = System.currentTimeMillis()
	}

	private def printTotalTimeTaken() {
		long timeTaken = System.currentTimeMillis() - timeStarted
		megaMessage("PROCESSING TOOK: ${timeTaken / 1000}s")
		true
	}

	private def megaMessage(message) {
		message = "# $message #"
		println('#' * message.size())
		println('#' + (' ' * (message.size() - 2)) + '#')
		println message
		println('#' + (' ' * (message.size() - 2)) + '#')
		println('#' * message.size())
	}

	private rando() {
		RANDOM.nextInt(500) + 257
	}

	private randomConnection() {
		def id = rando()
		def c = SmssyncFconnection.get(id)
		if(!c) {
			c = SmssyncFconnection.build()
		}
		return c
	}
}

