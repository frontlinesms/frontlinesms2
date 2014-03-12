package frontlinesms2.camel.clickatell

import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*

import grails.buildtestdata.mixin.Build

@Mock(ClickatellFconnection)
@Build(ClickatellFconnection)
class ClickatellPreProcessorSpec extends CamelUnitSpecification {
	ClickatellPreProcessor p
	
	def setup() {
		p = new ClickatellPreProcessor()
	}
	
	def 'out_body should be set to message text'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			1 * x.in.setBody("simple")
	}
	
	def 'out_body should be URL-encoded'() {
		setup:
			buildTestConnection()
			def x = mockExchange("more complex")
		when:
			p.process(x)
		then:
			1 * x.in.setBody("more+complex")
	}
	
	def 'dispatch ID should be set in header'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'frontlinesms.dispatch.id' == '45678'
	}
	
	def 'message destination should be set and stripped of leading plus'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.dst' == '1234567890'
	}
	
	def 'clickatell auth details should be set in header'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.apiId' == '11111'
			x.in.headers.'clickatell.username' == 'bob'
			x.in.headers.'clickatell.password' == 'secret'
			x.in.headers.'clickatell.fromNumber' == null
		
	}

	def 'clickatell fromNumber should be set in header if sendToUsa is true'() {
		setup:
			buildTestConnection(true)
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.apiId' == '11111'
			x.in.headers.'clickatell.username' == 'bob'
			x.in.headers.'clickatell.password' == 'secret'
			x.in.headers.'clickatell.fromNumber' == '%2B123321'
	}

	@Unroll
	def 'Messages containing characters that are not in the GSM Alphabet should be hex-encoded and sent with unicode=1'() {
		setup:
			buildTestConnection(true)
			def x = mockExchange(messageText)
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.unicode' == (expectUnicode? '1' : '0')
			1 * x.in.setBody(expectedBody)
		where:
			messageText                 | expectUnicode | expectedBody
			'simple'                    | false         | 'simple'
			'more complex'              | false         | 'more+complex'
			'123@#.+_*^'                | false         | '123%40%23.%2B_*%5E'
			'香川真司'                  | true          | 'feff99995ddd771f53f8'
			'Στυλιανός Γιαννακόπουλος'  | true          | 'feff03a303c403c503bb03b903b103bd03cc03c20020039303b903b103bd03bd03b103ba03cc03c003bf03c503bb03bf03c2'
			'박지성'                    | true          | 'feffbc15c9c0c131'
	}

	@Unroll
	def 'The concat variable is properly assigned a value based on the size of the message and the characters it contains'() {
		setup:
			buildTestConnection(true)
			def x = mockExchange(messageText)
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.concat' == expectedConcat
		where:
			messageText                 | expectedConcat
			'159 characters of text, which should be neatly wrapped in a single message payload and sent with concat set to one because GSM allows 160 chars in one message.'                | 1
			'160 characters of text, which should be neatly wrapped in a single message payload and sent with concat setting as 1 because GSM allows 160 chars in one message'               | 1
			'161 characters of text, which should be neatly wrapped in a single message payload and sent with concat setting as 2 because GSM allows 160 chars in one message.'              | 2
			"Let's test the 3-message border, shall we? Let's see.. to test a message that is the maximum size for concat value of 2 requires a message whose size is 163 + 162, which, if my maths doesn't fail me, is a total of three hundred and twenty five characters. That's quite a large target to get to, but let's see if we can do it."    | 2
			"And now, to test a monster message that is 326 characters long, which requires 3 messages. To get to that limit is a monster challenge. I've been doing pretty well to use 'real text' thus far, so I think I've earned the right to ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et"    | 3
			'Interestingly, some chars take two spaces in the payload. Examples of these are square brackets [], circumflex ^, and the left and right curly brackets {}.'                    | 1
			'If we get close to the 160 chars and include special characters, like the aforementioned [], circumflex ^, and the curly brackets {}, we might have to send two'                | 2
			'Just to spice things up, when ū and ì come along, our limit becomes 70'      | 1
			'Just to spice things up, when ū and ì come along, our limit becomes 70.'      | 2
			'That means a relatively short text mentioning German footballers like Mūller, Özil, Mertesaker and such might require a 2-message payload'      | 2
			'12345678@#.+_*^香川真司Στυλιανός Γιαννακόπουλος박지성 THis message is exactly 126 characters long, so will require concat value of 2.'      | 2
			'123@#.+_*^香川真司Στυλιανός Γιαννακόπουλος박지성 THis message is only 127 characters long, but will in fact require concat value of 3.'     | 3
	}

	private ClickatellFconnection buildTestConnection(sendToUsa=false) {
		if (sendToUsa)
			ClickatellFconnection.build(apiId:'11111', username:'bob', password:'secret', sendToUsa: true, fromNumber: "+123321")
		else
			ClickatellFconnection.build(apiId:'11111', username:'bob', password:'secret')
	}
}
