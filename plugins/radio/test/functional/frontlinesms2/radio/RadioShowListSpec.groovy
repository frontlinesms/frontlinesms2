package frontlinesms2.radio

class RadioShowListSpec extends RadioBaseSpec {
	
	def 'new messages in the current section are checked for in the background every ten seconds and causes a notification to appear if there are new messages'() {
		when:
			createRadioShows()
			to PageMorningShow
		then:
			at PageMorningShow
			visibleMessageTotal == 1
		when:
			sleep 11000
		then:
			visibleMessageTotal == 1
			!newMessageNotification.displayed
		when:
			createTestMessages()
			sleep 5000
		then:
			visibleMessageTotal == 1
			!newMessageNotification.displayed
		when:
			sleep 5000
		then:
			waitFor { newMessageNotification.displayed }
	}
	
//FIXME Tests break when ran together but pass when ran individually
//	def 'clicking the new message notification refreshes the list and removes the notification'() {
//		when:
//			createRadioShows()
//			to PageMorningShow
//		then:
//			at PageMorningShow
//			visibleMessageTotal == 1
//		when:
//			createTestMessages()
//			js.checkForNew()
//		then:
//			waitFor(17) { newMessageNotification.displayed }
//			visibleMessageTotal == 1
//		when:
//			newMessageNotification.find("a").click()
//		then:
//			waitFor { visibleMessageTotal == 3 }
//			!newMessageNotification.displayed
//	}
//		
//	def 'when clicking the new message notification, the view stays at the current page and details'() {
//		when:
//			createRadioShows()
//			to PageMorningShow
//		then:
//			at PageMorningShow
//			visibleMessageTotal == 1
//		when:
//			createTestMessages()
//			js.checkForNew()
//		then:
//			waitFor(17) { newMessageNotification.displayed }
//		when:
//			newMessageNotification.click()
//		then:
//			at PageMorningShow
//	}
}