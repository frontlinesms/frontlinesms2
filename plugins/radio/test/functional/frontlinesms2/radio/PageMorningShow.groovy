package frontlinesms2.radio

class PageMorningShow extends geb.Page {
	static url = "message/radioShow/${RadioShow.findByName('Morning Show').id}"
	static at = {
		title.endsWith('RadioShow')
	}
	
	static content = {
		onAir { $('#on-air')}
		startShow { $('#start-show')}
	}
}
