package frontlinesms2.radio

import java.util.Date
import frontlinesms2.*

class RadioShow extends MessageOwner {
	String name
	boolean isRunning
	Date dateCreated

	static constraints = {
		name(blank: false, nullable: false, unique: true, validator: { val, obj ->
			if(!obj.id) {
				return RadioShow.findByNameIlike(val) == null
			}
		})
	}

	def getShowMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
	
	def start() {
		if(!RadioShow.findByIsRunning(true)) {
			this.isRunning = true
		}
	}
	
	def stop() {
		isRunning = false
	}
}
