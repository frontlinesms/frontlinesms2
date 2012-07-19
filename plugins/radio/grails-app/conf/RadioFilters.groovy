import frontlinesms2.radio.*
import frontlinesms2.Activity
class RadioFilters {
	def filters = {
		justMessage(action:'*') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows(), radioShowActivityInstanceList: RadioShow.getAllRadioActivities()]
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
					addActivityToRadioShow(params.ownerId, params.radioShowId)
				}
			}
		}

		justActivity(action:'create') {
			after = { model ->
				params.radioSelector = true
			}
		}

		justActivity(action:'edit') {
			after = { model ->
				params.radioSelector = true
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAllByDeleted(false)
	}
	
	private def addActivityToRadioShow(ownerId, id) {
		def showInstance = RadioShow.get(id)
		def activityInstance
		ownerId? (activityInstance = Activity.get(ownerId)) : (activityInstance = Activity.get(model.ownerId))
		if(showInstance) {
			showInstance.addToActivities(activityInstance)
			showInstance.save(flush:true, failOnError:true)
		}
	}
	
}
