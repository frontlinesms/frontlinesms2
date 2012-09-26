package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class WebConnectionCedSpec extends WebConnectionBaseSpec {
	def "can launch web connection create wizard from create new activity link"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
	}
}

