package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import frontlinesms2.*

@TestFor(MessageSendService)
class MessageSendServiceSpec extends Specification {
	MessageSendService s

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
}

