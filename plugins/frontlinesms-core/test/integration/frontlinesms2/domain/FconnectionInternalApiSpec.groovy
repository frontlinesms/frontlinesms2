package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class FconnectionInternalApiSpec extends grails.plugin.spock.IntegrationSpec {
	def grailsApplication

	@Unroll
	def '#c.class.simpleName routes should have fconnection-id header set'() {
		expect:
			c.routeDefinitions.every {
				it.toString().contains('SetHeader[fconnection-id, simple{Simple: 123}]')
			}
		where:
			c << getAllFconnectionImplementations()
	}

	@Unroll
	def '#c.class.simpleName routes should have id set including database id'() {
		expect:
			c.routeDefinitions.every { [(it.id): it.id ==~ /(in|out)-(.*-)?\d+/] }
		where:
			c << getAllFconnectionImplementations()
	}

	private def getAllFconnectionImplementations() {
		return grailsApplication.domainClasses.findAll { it.name.endsWith("Fconnection") }.collect {
			def c = it.clazz.build()
			c.id = 123
			return c
		}
	}
}

