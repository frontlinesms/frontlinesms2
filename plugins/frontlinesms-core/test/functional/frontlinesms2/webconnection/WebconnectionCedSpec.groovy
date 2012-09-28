package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class WebconnectionCedSpec extends WebconnectionBaseSpec {
	def "can launch web connection create wizard from create new activity link"() {
		expect:
			launchWizard()
	}
}

