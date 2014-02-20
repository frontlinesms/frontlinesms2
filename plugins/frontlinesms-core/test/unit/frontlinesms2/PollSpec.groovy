package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

import grails.buildtestdata.mixin.Build

@TestFor(Poll)
@Mock([PollResponse, MessageDetail])
@Build(TextMessage)
class PollSpec extends Specification {
	/** some responses that should pass validation */
	def OK_RESPONSES = [new PollResponse(value:'one', key:'A'), new PollResponse(value:'two', key:'B')]
	private static final String TEST_NUMBER = "+2345678"
	
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		PollResponse.metaClass.removeFromMessages = { m ->
			delegate.messages.remove(m)
			m.messageOwner = null
		}

		Activity.metaClass.static.get = {Long id -> 
			Poll.findById(id)
		}

		
	}

	@Unroll
	def 'poll must have at least three responses'() {
		given:
			def p = new Poll(name:'test poll')
			p.responses = [] as SortedSet
			responseCount.times { p.responses << new PollResponse(value:"r-$it", key:"$it") }
		expect:
			p.validate() == valid
		where:
			responseCount | valid
			0             | false
			1             | false
			2             | false
			3             | true
	}

	def "poll auto-reply cannot be blank"() {
		when:
			def poll = new Poll(title:"title", autoReplyText:" ", responses:OK_RESPONSES)
		then:
			!poll.validate()
	}

	@Unroll
	def 'processKeyword should assign messages to the appropriate response'() {
		given:
			def pollAndResponses = createPoll(validResponseCount)
			def poll = pollAndResponses.poll
			def responses = pollAndResponses.responses
			def m = TextMessage.build(text:messageText)
			def k = new Keyword(value:'irrelevant', ownerDetail:keywordOwnerDetail, isTopLevel:!keywordOwnerDetail)
		when:
			poll.processKeyword(m, k)
		then:
			responses[response].messages == [m]
			poll.messages?.contains(m)
		where:
			messageText            | keywordOwnerDetail | validResponseCount | response
			'k c'                  | 'C'                | 3                  | 'C'
			'k'                    | null               | 3                  | Poll.KEY_UNKNOWN
			'word a'               | 'A'                | 3                  | 'A'
			'word b'               | 'B'                | 3                  | 'B'
			'word c'               | 'C'                | 3                  | 'C'
			'word d'               | null               | 3                  | Poll.KEY_UNKNOWN
			'word averylongword'   | null               | 3                  | Poll.KEY_UNKNOWN
			'    word a response ' | 'A'                | 3                  | 'A'
			'\r\nword a match'     | 'A'                | 3                  | 'A'
			'wordA'                | 'A'                | 3                  | 'A'
			'wordA with more words'| 'A'                | 3                  | 'A'
			'oneword'              | null               | 3                  | Poll.KEY_UNKNOWN
			'oneword'              | null               | 3                  | Poll.KEY_UNKNOWN
			'two words'            | null               | 3                  | Poll.KEY_UNKNOWN
			'two bords'            | null               | 3                  | Poll.KEY_UNKNOWN
			'keyword c'            | null               | 2                  | Poll.KEY_UNKNOWN
	}

	def 'processKeyword should send autoreply if one is present'() {
		given:
			def poll = createPoll(3).poll
			def sendService = Mock(MessageSendService)
			poll.messageSendService = sendService
			poll.autoreplyText = "some reply text"
			poll.save(failOnError:true, flush:true)
			
			def replyMessage = TextMessage.build(text:"woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some reply text'
			}) >> replyMessage

			def pollService = Mock(PollService)
			pollService.sendPollReply(_,_) >> { sendService.send(replyMessage)}
			poll.pollService = pollService

			def inMessage = TextMessage.build(text:"message text", src:TEST_NUMBER)
		when:
			poll.processKeyword(inMessage, new Keyword(value:'test', isTopLevel:true, ownerDetail:null))
		then:
			1 * sendService.send(replyMessage)
	}
	
	def 'edit responses should create responses which do no exist'() {
		given:
			def poll = new Poll()
			def params = [choiceA:'eh', choiceB:'bee',
					choiceC:'sea', choiceD:'dee']
		when:
			poll.editResponses(params)
		then:
			poll.responses*.key == ['A', 'B', 'C', 'D', Poll.KEY_UNKNOWN]
	}

	def 'removing a message from a poll should remove it from poll.messages'() {
		given:
			TextMessage m = new TextMessage()
			Poll p = new Poll()
		when:
			p.removeFromMessages(m)
		then:
			!p.messages
	}

	@Unroll
	def 'ill-formated submitted aliases should be formated neatly'() {
		given:
			def p =  new Poll()
		expect:
			p.extractAliases(attrs, 'B') == validAliases
		where:
			attrs                          | validAliases
			[aliasB: ",,,,"]               | ""
			[aliasB: "   "]                | ""
			[aliasB: "a,,,b"]              | "A, B"
			[aliasB: ",,,,a  ,,,,b,,,   "] | "A, B"
	}

	private def createPoll(int validResponseCount) {
		def p = new Poll()
		p.name = "test poll"
		def responses = [unknown:PollResponse.createUnknown()]
		p.addToResponses(responses.unknown)
		for(i in 0..<validResponseCount) {
			def key = ('A'..'C')[i]
			def r = new PollResponse(key:key, value:"mock-response-$i")
			responses[key] = r
			p.addToResponses(r)
		}
		p.save(failOnError:true, flush:true)
		return [poll:p, responses:responses]
	}
}

