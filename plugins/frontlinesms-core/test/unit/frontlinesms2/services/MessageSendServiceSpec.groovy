package frontlinesms2.services

import spock.lang.*

import frontlinesms2.*

@TestFor(MessageSendService)
@Mock([Fmessage, Dispatch])
class MessageSendServiceSpec extends Specification {
	MessageSendService s

	def setup() {
		// for some reason mocking Fmessage and Dispatch does not add Fmessage.addToDispatches method
		Fmessage.metaClass.addToDispatches = { d ->
			if(!delegate.dispatches) delegate.dispatches = []
			delegate.dispatches << d
		}
	}

	@Unroll
	def 'generateDispatches should generate a single dispatch for each of a list of addresses'() {
		expect:
			service.generateDispatches(suppliedAddresses).status.every { it == DispatchStatus.PENDING }
			service.generateDispatches(suppliedAddresses)*.dst.sort() == expectedAddresses
		where:
			suppliedAddresses | expectedAddresses
			[]                | []
			[null]            | []
			['1234']          | ['1234']
			['1234', null]    | ['1234']
			['+1234']         | ['+1234']
			['1234', '56789'] | ['1234', '56789']
			['1234', '1234']  | ['1234']
	}

	@Unroll
	def 'createOutgoingMessage should strip unexpected characters from supplied phone number'() {
		expect:
			service.createOutgoingMessage([addresses:unsanitised]).dispatches*.dst == [sanitised]
		where:
			unsanitised     | sanitised
			'123456789'     | '123456789'
			'123 456 789'   | '123456789'
			'+123456789'    | '+123456789'
			'(123)-456-789' | '123456789'
	}
}

