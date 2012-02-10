package frontlinesms2.radio

import frontlinesms2.*
import java.util.Date

class RadioShowISpec extends grails.plugin.spock.IntegrationSpec {

	def setup() {
		def radioShow = new RadioShow(name: "Health & fitness")
		radioShow.addToMessages(new Fmessage(src:"123", date:new Date(), text: "eat fruits", inbound:true, starred : true))
		radioShow.addToMessages(new Fmessage(src:"123", date:new Date(), text: "eat vegetables", inbound:true))
		radioShow.addToMessages(new Fmessage(src:"123", date:new Date(), text: "excerise", inbound:true))
		radioShow.save(failOnError: true, flush: true)
	}

	def "should fetch all messages for a radio show"() {
		when:
			def results = RadioShow.findByName("Health & fitness").getShowMessages(false).list()
		then:
			results*.text.containsAll(["eat fruits", "eat vegetables", "excerise"])
	}

	def "should fetch all messages based on offset and max parameters"() {
		when:
			def results = RadioShow.findByName("Health & fitness").getShowMessages(false).list(max:2, offset: 0)
		then:
			results.size() == 2
	}

	def "should fetch all starred radio show messages"() {
		when:
			def results = RadioShow.findByName("Health & fitness").getShowMessages(true).list()
		then:
			results*.text == ["eat fruits"]
	}

	def "should count starred radio show messages"() {
		when:
			def starredHealthShowMessages = RadioShow.findByName("Health & fitness").getShowMessages(true)
			def unstarredHealthShowMessages = RadioShow.findByName("Health & fitness").getShowMessages(false)
		then:
			starredHealthShowMessages.count() == 1
			unstarredHealthShowMessages.count() == 3
	}
	
	def "radioShows can be associated with one or more polls"() {
		when:
			def show = RadioShow.findByName("Health & fitness")
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(name: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
			show.addToPolls(poll)
			show.addToPolls(poll2)
			show.save(flush:true)
			show.refresh()
		then:
			show.polls.size() == 2
	}
	
	def "deleted polls are nolonger listed with radioShows"() {
		when:
			def show = RadioShow.findByName("Health & fitness")
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(name: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
			show.addToPolls(poll)
			show.addToPolls(poll2)
			show.save(flush:true)
		then:
			show.activePolls.size() == 2
		when:
			poll.deleted = true
		then:
			show.activePolls.size() == 1
	}
	
	def "archived polls are nolonger listed with radioShows"() {
		when:
			def show = RadioShow.findByName("Health & fitness")
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(name: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
			show.addToPolls(poll)
			show.addToPolls(poll2)
			show.save(flush:true)
		then:
			show.activePolls.size() == 2
		when:
			poll.archived = true
		then:
			show.activePolls.size() == 1
	}
	
	def "message searches can be restricted to a radio show"() {
		given:
			def show = RadioShow.findByName("Health & fitness")
			def m = new Fmessage(src:"123", date:new Date(), text: "eat fruits now", inbound:true).save(flush:true)
			def controller = new SearchController()
		when:
			controller.params.searchString = "fruits"
			controller.params.activityId = "folder-${show.id}"
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findByText('eat fruits')]
	}
}
