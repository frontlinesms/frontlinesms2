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
					addActivityToRadioShow(model, params.radioShowId)
				}
			}
		}
		
		justPoll(action:'edit') {
			after = { model ->
				if(params.radioShowId) {
					addActivityToRadioShow(model, params.radioShowId)
				}
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAll()
	}
	
	private def addActivityToRadioShow(model, id) {
		def showInstance = RadioShow.get(id)
		def activityInstance = Activity.get(model.ownerId)
		if(showInstance) {
			removeActivityFromRadioShow(activityInstance)
			showInstance.addToActivity(activityInstance)
		}
		showInstance.save(flush:true, failOnError:true)
		println "${activityInstance.name} has been added to ${showInstance.name}"
	}
	
}
