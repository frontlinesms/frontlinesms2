package frontlinesms2.radio

class PageMorningShow extends geb.Page {
	static url = "message/radioShow/${RadioShow.findByName('Morning Show').id}"
	static at = {
		title.endsWith('RadioShow')
	}
	
	static content = {
		MessagePageOnAirNotice { $('#on-air')}
		startShow { $('#radio-actions').find('a.start-show')}
		stopShow { $('#radio-actions').find('a.stop-show')}
		ShowListOnAirNotice {id ->
					$("#show-$id")
			}
	}
}
