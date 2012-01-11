import frontlinesms2.radio.*
import frontlinesms2.Poll
class RadioFilters {
	def filters = {
		justMessage(controller:'message', action:'*') {
			after = { model ->
				if(model) model << [radioShows: listRadioShows()]
			}
		}
		
		justPoll(controller:'poll', action:'save') {
			after = { model ->
				if(params.radioShow) {
					println "ID returned is ${model.ownerId} and params.radioShow is ${params.radioShow}"
					def showInstance = RadioShow.get(params.radioShow)
					def poll = Poll.get(model.ownerId)
					showInstance.addToPolls(poll)
					showInstance.save(flush:true)
				}
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAll()
	}
}
