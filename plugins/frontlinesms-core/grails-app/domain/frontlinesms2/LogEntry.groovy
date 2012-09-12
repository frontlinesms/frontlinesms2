package frontlinesms2

class LogEntry {
	Date date
	String content
	
	static constraints = {
		date(blank: false, nullable: false)
		content(blank: false, nullable:false)
	}
	
	static def log(content) {
		new LogEntry(date: new Date(), content: content).save()
		return content
	}
}
