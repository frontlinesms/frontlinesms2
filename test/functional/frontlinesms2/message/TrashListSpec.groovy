package frontlinesms2.message

import java.text.DateFormat;
import java.text.SimpleDateFormat
import java.util.Date;

import frontlinesms2.*

class TrashListSpec extends frontlinesms2.poll.PollGebSpec {
	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	new Fmessage(src: "src1", dst: "dst1", deleted: true, starred: true).save(flush: true)
	    	new Fmessage(src: "src2", dst: "dst2", deleted: true).save(flush: true)
		when:
			go "message/trash"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'dst1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['dst1', 'dst2'])
	}
	
	def "should not contain export button" () {
		when:
			go "message/trash"
		then:
			!$('a', text:'Export')
	}
	
	def "deleted polls show up in the trash section"() {
		given:
		def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks", deleted: true).save(failOnError:true, flush:true)
	when:
		go "message/trash"
		$("a", text: "Who is badder?")
		def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		def formatedDate = dateToString(poll.lastUpdated)
	then:
		rowContents[2] == 'Who is badder?'
		rowContents[3] == '0 messages'
		rowContents[4] == formatedDate
	}
	
	def "selected poll and its details are displayed"() {
		given:
		def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks", deleted: true).save(failOnError:true, flush:true)
	when:
		go "message/trash"
		$("a", text: "Who is badder?").click()
	then:
		$('#activity-name').text() == poll.title
		$('#activity-date').text() == dateToString(poll.lastUpdated)
		$('#activity-body').text() == "${poll.getLiveMessageCount()} messages"
	}
	
	def "clicking on empty trash removes polls from the database"() {
		given:
		def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks", deleted: true).save(failOnError:true, flush:true)
	when:
		go "message/trash"
		$("a", text: "Who is badder?").click()
		$("#trash-actions").value("empty-trash")
	then:
		waitFor { $("#ui-dialog-title-modalBox").displayed }
	when:
		$("#title").value("Empty trash")
		$("#done").click()
	then:
		!Poll.findById(poll.id)
	}
	
	String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat();
		return formatedDate.format(date)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("dd MMMM, yyyy hh:mm", Locale.US)
	}
}



