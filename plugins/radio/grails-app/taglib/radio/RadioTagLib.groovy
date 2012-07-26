package frontlinesms2.radio

import frontlinesms2.Activity

class RadioTagLib {
	static namespace = 'radio'

	def selectShow = { attr ->
		out << "<div id='select-radio-show'>"
		out << "<label>Assign Activity to Radio Show</label>"
		if(attr.ownerInstance) {
			attr.radioShowIntance = RadioShow.findByOwnedActivity(attr.ownerInstance).list()[0]
		}
		out << select(attr)
		out << "</div>"
	}

	def select = { attr ->
		attr.class = "radio-show-select"
		attr.name = "radioShowId"
		attr.noSelection = ['':attr.radioShowIntance?g.message(code:'activity.assigned.defaultoption', args:[attr.radioShowIntance.name]):g.message(code:'activity.unassigned.defaultoption')]
		attr.optionKey = "id"
		attr.optionValue = "name"
		attr.value = attr.radioShowIntance?.id
		out << g.select(attr)
	}
}