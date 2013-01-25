package frontlinesms2.jobs

import spock.lang.*
import grails.test.mixin.*
import frontlinesms2.*

class UploadRegistrationDataSpec extends Specification {

	def job
	def testProps

	def setup() {
		job = new UploadRegistrationDataJob()
		job.metaClass.getRegistrationProperties = { -> testProps }
		job.metaClass.writeRegistrationPropertiesFile = { Properties properties -> println " I haven't really written to the registration.properties file" }
	}

	def "can upload registration data"() {
		given:
			testProps = new Properties()
			testProps.setProperty('registered','false')
			DataUploadService s = Mock()
			job.dataUploadService = s
		when:
			job.execute()
		then:
			1 * s.upload(_,_)
	}

	def "should not upload already uploaded registration data"() {
		given:
			testProps = new Properties()
			testProps.setProperty('registered','true')
			DataUploadService s = Mock()
			job.dataUploadService = s
		when:
			job.execute()
		then:
			0 * s.upload(_,_)
	}

	def "should not upload empty registration data"() {
		given:
			testProps = new Properties()
			DataUploadService s = Mock()
			job.dataUploadService = s
		when:
			job.execute()
		then:
			0 * s.upload(_,_)
	}

	def "should not upload null registration data"() {
		given:
			DataUploadService s = Mock()
			job.dataUploadService = s
		when:
			job.execute()
		then:
			0 * s.upload(_,_)
	}
}
