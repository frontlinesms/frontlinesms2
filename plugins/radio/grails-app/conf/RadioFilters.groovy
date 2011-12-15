import frontlinesms2.radio.*

class RadioFilters {
	def filters = {
		all(controller:'message', action:'*') {
			after = { model ->
				if(model) model << [radioShows: listRadioShows()]
			}
		}
	}
	
	def listRadioShows() {
		RadioShow.findAll()
	}
}
