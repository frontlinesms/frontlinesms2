package frontlinesms2.controller

import frontlinesms2.*

import spock.lang.*

class WebconnectionApiControllerISpec extends Specification {
	def 'request with bad content should return 400 status'() {
		given:
			def id = Webconnection.build(secret:'skyfall').id
			params = [entityClassApiUrl:'webconnection', secret:'skyfall', entityId:id]
		when:
			controller.index()
		then:
			response.status == 400
	}
}


