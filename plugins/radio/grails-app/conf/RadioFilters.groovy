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
				if(params.radioShowId) {
					def showInstance = RadioShow.get(params.radioShowId)
					def poll = Poll.get(model.ownerId)
					showInstance.addToPolls(poll)
					showInstance.save(flush:true, failOnError:true)
					println "${poll.title} has been added to ${showInstance.name}"
				}
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAll()
	}
}
