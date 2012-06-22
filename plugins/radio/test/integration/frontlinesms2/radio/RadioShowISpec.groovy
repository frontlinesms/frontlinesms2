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
			def poll = new Poll(name: 'Who is badder?')
			poll.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			poll.save(failOnError:true)
			def poll2 = new Poll(name: 'Who will win?')
			poll2.editResponses(choiceA: 'Uhuru', choiceB:'Ruto')
			poll2.save(failOnError:true)
			show.addToActivities(poll)
			show.addToActivities(poll2)
			show.save(failOnError:true)
		then:
			show.activities.size() == 2
	}
	
	def "deleted polls are nolonger listed with radioShows"() {
		when:
			def show = RadioShow.findByName("Health & fitness")
			def poll = new Poll(name: 'Who is badder?')
			poll.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			poll.save(failOnError:true)
			def poll2 = new Poll(name: 'Who will win?')
			poll2.editResponses(choiceA: 'Uhuru', choiceB:'Ruto')
			poll2.save(failOnError:true)
			show.addToActivities(poll)
			show.addToActivities(poll2)
			show.save()
		then:
			show.activeActivities.size() == 2
		when:
			poll.deleted = true
		then:
			show.activeActivities.size() == 1
	}
	
	def "archived polls are nolonger listed with radioShows"() {
		when:
			def show = RadioShow.findByName("Health & fitness")
			def poll = new Poll(name: 'Who is badder?',question: "question")
			poll.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			poll.save(failOnError:true)
			def poll2 = new Poll(name: 'Who will win?', question: "politics")
			poll2.editResponses(choiceA: 'Uhuru', choiceB:'Ruto')
			poll2.save(failOnError:true)
			show.addToActivities(poll)
			show.addToActivities(poll2)
			show.save()
		then:
			show.activeActivities.size() == 2
		when:
			poll.archived = true
		then:
			show.activeActivities.size() == 1
	}
	
	def "message searches can be restricted to a radio show"() {
		given:
			def show = RadioShow.findByName("Health & fitness")
			def m = new Fmessage(src:"123", date:new Date(), text: "eat fruits now", inbound:true).save()
			def controller = new SearchController()
		when:
			controller.params.searchString = "fruits"
			controller.params.activityId = "folder-${show.id}"
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findByText('eat fruits')]
	}

	def "radioShow can own one more activities"() {
		setup:
			def show = new RadioShow(name:"Health Show").save()
			def poll = new Poll(name:"Test Poll")
			poll.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			poll.save(failOnError:true)
			def announcement = Announcement.build()
		when:
			show.addToActivities(poll)
			show.addToActivities(announcement)
		then:
			show.activities.size() == 2
	}
}
