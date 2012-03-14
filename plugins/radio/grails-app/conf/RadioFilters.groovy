import frontlinesms2.radio.*
import frontlinesms2.Poll
class RadioFilters {
	def filters = {
		justMessage(action:'*') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows(), radioShowPollInstanceList: RadioShow.getAllRadioPolls()]
					}
			}
		}
		
		justSearch(action:'*') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows()]
					}
			}
		}
		
		justStatus(action:'show') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows()]
					}
			}
		}
		
		justPoll(action:'save') {
			after = { model ->
				if(params.radioShowId) {
					addPollToRadioShow(model, params.radioShowId)
				}
			}
		}
		
		justPoll(action:'edit') {
			after = { model ->
				if(params.radioShowId) {
					addPollToRadioShow(model, params.radioShowId)
				}
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAll()
	}
	
	private def addPollToRadioShow(model, id) {
		def showInstance = RadioShow.get(id)
		def pollInstance = Poll.get(model.ownerId)
		if(showInstance) {
			removePollFromRadioShow(pollInstance)
			showInstance.addToPolls(pollInstance)
		}
		showInstance.save(flush:true, failOnError:true)
		println "${pollInstance.name} has been added to ${showInstance.name}"
	}
	
	private void removePollFromRadioShow(pollInstance) {
		RadioShow.findAll().collect { showInstance ->
			if(pollInstance in showInstance.polls) {
				showInstance.removeFromPolls(pollInstance)
				showInstance.save()
			}
		}
	}
}
