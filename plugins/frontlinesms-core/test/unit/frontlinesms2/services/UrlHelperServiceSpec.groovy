package frontlinesms2.services

import spock.lang.*
import frontlinesms2.*

@TestFor(UrlHelperService)
class UrlHelperServiceSpec extends Specification {
	def context

	def setup() {
		service.i18nUtilService = [getMessage: { args ->
			return args.code
		}]
	}

	def 'getBaseUrl() will use request object\'s scheme, domain and port to generate url'() {
		given:
			def request = [scheme: 'HTTP', serverPort: 12345, serverName: 'example.com']
		when:
			def url = service.getBaseUrl(request)
		then:
			url == 'http://example.com:12345'
	}

	@Unroll
	def 'ports are not explicit in generated url unless they are non-default for the scheme'() {
		given:
			def request = [scheme: scheme, serverPort: port, serverName: 'example.com']
		expect:
			service.getBaseUrl(request) == expectedUrl
		where:
			scheme  | port | expectedUrl
			'HTTP'  | 80   | 'http://example.com'
			'HTTP'  | 123  | 'http://example.com:123'
			'HTTPS' | 443  | 'https://example.com'
			'HTTPS' | 123  | 'https://example.com:123'
	}

	def 'localhost server name results in [your-ip-address] in url string'() {
		given:
			def request = [scheme: 'HTTP', serverPort: 12345, serverName: 'localhost']
		when:
			def url = service.getBaseUrl(request)
		then:
			url == 'http://&lt;localhost.ip.placeholder&gt;:12345'
	}

}
