package frontlinesms2

import spock.lang.*

@TestFor(SystemNotification)
class SystemNotificationSpec extends Specification {
	@Unroll
	def 'creating a system notification should automatically truncate text'() {
		expect:
			new SystemNotification(text:supplied).text == expected
		where:
			supplied  | expected
			'asdf'    | 'asdf'
			'x' * 511 | 'x' * 511
			'x' * 512 | ('x' * 510) + '…'
	}
}

