import spock.lang.*

@TestFor(CoreUrlMappings)
@Mock(frontlinesms2.ApiController)
class ApiUrlMappingSpec extends Specification {
	def testApiUrlMappingWithoutSecret() {
		expect:
			assertForwardUrlMapping('/api/1/smssync/123', controller:'api', action:'index') {
				entityClassApiUrl = 'smssync'
				entityId = '123'
			}
	}

	def testApiUrlMappingWithSecret() {
		expect:
			assertForwardUrlMapping('/api/1/smssync/123/password', controller:'api', action:'index') {
				entityClassApiUrl = 'smssync'
				entityId = '123'
				secret = 'password'
			}
	}
}

