package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build


@TestFor(QueuedDispatch)
@Build([Fconnection, Dispatch])
class QueuedDispatchSpec extends Specification {
	def 'create should save a QueuedDispacth'() {
		when:
			QueuedDispatch.create(Fconnection.build(), Dispatch.build(), true)
		then:
			QueuedDispatch.getAll().size() == 1
	}
}
