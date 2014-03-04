package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build


@TestFor(QueuedDispacth)
@Build(Dispatch)
class QueuedDispatchSpec extends Specification {
	def 'create should save a QueuedDispacth'() { throw RuntimeException('bad') }
	def 'getDispatched should get all the dispatches associated with a connection'() { throw RuntimeException('bad') }
}
