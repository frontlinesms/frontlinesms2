import frontlinesms2.radio.*
import frontlinesms2.Activity
import frontlinesms2.*
class RadioFilters {
	def filters = {
		justMessage(action:'*') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows(), radioShowActivityInstanceList: RadioShow.getAllRadioActivities()]
				}
				return true
			}
		}
		
		justSearch(action:'*') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows()]
				}
				return true
			}
		}
		
		justStatus(action:'show') {
			after = { model ->
				if(model) {
					model << [radioShowInstanceList: listRadioShows()]
				}
				return true
			}
		}
		
		justActivity( action:'save') {
			after = { model ->
				if(params.radioShowId) {
					addActivityToRadioShow(params.activityId, params.radioShowId)
				}
				return true
			}
		}

		justArchive(action:'activityList') {
			after = { model ->
				model.activityInstanceList -= model.activityInstanceList.findAll { act -> RadioShow.findByOwnedActivity(act).get()?.archived }
				model.activityInstanceTotal = model.activityInstanceList?.size()
				return true
			}
		}

		forActivityInShowArchive(controller:'archive', action:'activity') {
			after = { model ->
				model.inARadioShow = RadioShow.findByOwnedActivity(model?.ownerInstance)?.get()?.archived
				return true
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAllByDeletedAndArchived(false, false)
	}
	
	private def addActivityToRadioShow(activityId, id) {
		def showInstance = RadioShow.get(id)
		def activityInstance
		activityId? (activityInstance = Activity.get(activityId)) : (activityInstance = Activity.get(model.activityId))
		if(showInstance) {
			showInstance.addToActivities(activityInstance)
			showInstance.save(flush:true, failOnError:true)
		}
	}
	
}
