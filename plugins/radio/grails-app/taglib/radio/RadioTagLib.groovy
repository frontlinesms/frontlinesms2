package frontlinesms2.radio

import frontlinesms2.Activity

class RadioTagLib {
	static namespace = 'radio'

	def selectShow = { att ->
		out << "<div id='select-radio-show' class='input'>"
println "att.hideLabel?.toBoolean() = ${att.hideLabel?.toBoolean()}"
		if(!(att.hideLabel?.toBoolean())) {
			println "att.hideLabel = $att.hideLabel (${att.hideLabel.getClass()})"
			out << "<label>Assign Activity to Radio Show</label>"
		}
		if(att.activityInstance) {
			att.radioShowInstance = RadioShow.findByOwnedActivity(att.activityInstance).list()[0]
		}
		if(att.wrapInFormTag) {
			out << g.form(controller:"radioShow", action:"addActivity") {
				out << g.hiddenField(name:"activityId", value:att.activityInstance?.id)
				selectShow_selecter(att)
			}
		} else {
			selectShow_selecter(att)
		}
		out << "</div>"
	}

	private def selectShow_selecter = { att ->
		att.class = "radio-show-select"
		att.name = "radioShowId"
		att.noSelection = ['':att.radioShowInstance?
				g.message(code:'activity.assigned.defaultoption', args:[att.radioShowInstance.name]):
				g.message(code:'activity.unassigned.defaultoption')]
		att.optionKey = "id"
		att.optionValue = "name"
		att.value = att.radioShowInstance?.id
		["activityInstance", "radioShowInstance", 'showLabel', "wrapInFormTag"].each { att.remove(it) }
		out << g.select(att)
	}

	def confirmRadioRow = {att ->
		def message = g.message(code:"radioShow.name.none")
		if(att.activityInstance) {
			def radioShowInstance = RadioShow.findByOwnedActivity(att.activityInstance).list()[0]
			if(radioShowInstance) message = radioShowInstance.name
		}
		out << '<tr>'
		out << "<td>${g.message(code:"radioShow.name.label")} </td>"
		out << "<td id='radioShow-confirm'>"
		out << message
		out << "</td>"
		out << '</tr>'
	}
}
