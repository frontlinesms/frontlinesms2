package frontlinesms2.radio

import frontlinesms2.*

class RadioShowISpec extends grails.plugin.spock.IntegrationSpec {

	def setup() {
		def radioShow = new RadioShow(name: "Health & fitness")
		radioShow.addToMessages(new Fmessage(text: "eat fruits", starred : true))
		radioShow.addToMessages(new Fmessage(text: "eat vegetables"))
		radioShow.addToMessages(new Fmessage(text: "excerise"))
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
			def results = RadioShow.findByName("Health & fitness").getShowMessages(['starred':true]).list()
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
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(title: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
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
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(title: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
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
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(title: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
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
	
}
