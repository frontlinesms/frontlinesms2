package frontlinesms2.radio

import java.util.Date
import frontlinesms2.*

class RadioShow extends MessageOwner {
	String name
	boolean isRunning
	Date dateCreated
	List polls
	static hasMany = [polls: Poll]
	
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
		} else {
			return false
		}
	}
	
	def stop() {
		isRunning = false
	}
	
	def getActivePolls() {
		def pollList
		if(polls) {
			pollList = Poll.withCriteria {
				'in'("id", polls*.id)
				eq("archived", false)
				eq("deleted", false)
			}
		}
		pollList
	}
	
	static def getAllRadioPolls() {
		def radioPollsInstanceList = RadioShow.findAll()*.polls
		radioPollsInstanceList.flatten()
	}
}
