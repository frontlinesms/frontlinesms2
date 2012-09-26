package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class WebConnectionCedSpec extends WebConnectionBaseSpec {
	def "can launch web connection create wizard from create new activity link"() {
		expect:
			launchWizard()
	}
}

