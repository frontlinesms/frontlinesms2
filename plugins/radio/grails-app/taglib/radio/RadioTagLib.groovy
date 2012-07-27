package frontlinesms2.radio

import frontlinesms2.Activity

class RadioTagLib {
	static namespace = 'radio'

	def selectShow = { att ->
		out << "<div id='select-radio-show' class='input'>"
		out << "<label>Assign Activity to Radio Show</label>"
		if(att.ownerInstance) {
			att.radioShowIntance = RadioShow.findByOwnedActivity(att.ownerInstance).list()[0]
		}
		if(att.formtag) {
			out << g.form(controller:"radioShow", action:"addActivity") {
				out << g.hiddenField(name:"activityId", value:att.ownerInstance?.id)
				out << select(att)
			}
		} else {
			out << select(att)
		}
		out << "</div>"
	}

	def select = { att ->
		att.class = "radio-show-select"
		att.name = "radioShowId"
		att.noSelection = ['':att.radioShowIntance?g.message(code:'activity.assigned.defaultoption', args:[att.radioShowIntance.name]):g.message(code:'activity.unassigned.defaultoption')]
		att.optionKey = "id"
		att.optionValue = "name"
		att.value = att.radioShowIntance?.id
		out << g.select(att)
	}
}