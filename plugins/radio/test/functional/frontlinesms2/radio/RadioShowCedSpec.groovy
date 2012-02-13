package frontlinesms2.radio

import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class RadioShowCedSpec extends RadioBaseSpec {

	def "should be able to create new shows"() {
		when:
			go "message"
			$("a", text: "Create new show").click()
			waitFor { $("#modalBox").displayed }
			$("input", name: 'name').value("show name")
			$("#done").click()
			waitFor { $("a", text:"show name").displayed }
		then:
	       	$("a", text:"show name").displayed
	}

	def "should throw validation errors when name is not given for a show"() {
		when:
			go "message"
			$("a", text: "Create new show").click()
			waitFor { $("#modalBox").displayed }
			$("input", name: 'name').value("")
			$("#done").click()
		then:
			println "flash message:" + $("div.flash").text()
			waitFor { $("div.flash").text().contains("Name is not valid") }
	}
	
	def "separator is displayed for radio messages from different days"() {
		given:
			def show = new RadioShow(name:"Test").save()
			def messageA = new Fmessage(src: '+3245678', inbound:true, text: "What is diabetes?", date: new Date() - 2).save(failOnError: true)
			def messageB = new Fmessage(src: 'Jill',  inbound:true, text: "I love life", date: new Date() - 1).save(failOnError: true)
			show.addToMessages(messageA)
			show.addToMessages(messageB)
			show.save(failOnError: true, flush: true)
		when:
			go "message/radioShow/${show.id}"
		then:
			$(".message-list-separator").text() == "${dateToString(new Date()-2)}"
	}

//FIXME Test fails when all the test-app is run
//	def "'on air' notice does not have 'active' css when radio show is stopped"() {
//		given:
//			createRadioShows()
//		when:
//			to PageMorningShow
//		then:
//			at PageMorningShow
//			!MessagePageOnAirNotice.classes()
//		when:
//			assert RadioShow.findByName("Morning Show")
//			println "${startShow.text()}"
//			startShow.click()
//		then:
//			waitFor { MessagePageOnAirNotice.hasClass('active')}
//			ShowListOnAirNotice(RadioShow.findByName("Morning Show").id).hasClass('active')
//		when:
//			stopShow.click()
//		then:
//			waitFor { !MessagePageOnAirNotice.classes()}
//			!ShowListOnAirNotice(RadioShow.findByName("Morning Show").id).hasClass('active')
//			
//	}
	
	def "'on air' notice is shown against active radio show in show list"() {
		given:
			createRadioShows()
			def show = new RadioShow(name:"Health Show")
			show.start()
			show.save(flush:true)
		when:
			to PageMorningShow
		then:
			at PageMorningShow
			ShowListOnAirNotice(RadioShow.findByName("Health Show").id).hasClass('onAirIsActive')
	}
	
	private String dateToString(Date date) {
		new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(date)
	}
}

