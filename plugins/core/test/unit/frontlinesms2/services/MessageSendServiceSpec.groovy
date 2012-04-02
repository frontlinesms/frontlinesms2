package frontlinesms2.services

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class MessageSendServiceSpec extends UnitSpec {
	MessageSendService s

	def setup() {
		s = new MessageSendService()
	}

	@Unroll
	def 'generateDispatches should generate a single dispatch for each of a list of addresses'() {
		expect:
			s.generateDispatches(suppliedAddresses).status.every { it == DispatchStatus.PENDING }
			s.generateDispatches(suppliedAddresses)*.dst.sort() == expectedAddresses
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

