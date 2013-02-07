package frontlinesms2

import org.springframework.web.servlet.support.RequestContextUtils
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException

class FsmsTagLib {
	static namespace = 'fsms'

	def appSettingsService
	def expressionProcessorService
	def grailsApplication
	def i18nUtilService 

	def info = { att ->
		def cssClass = 'info'
		if(att.class) cssClass += ' ' + att.class
		out << "<p class='$cssClass'>"
		out << g.message(code:att.message)
		out << '</p>'
	}

	def ifAppSetting = { att, body ->
		if(Boolean.parseBoolean(appSettingsService[att.test])) {
			out << body()
		}
	}

	def wizard = { att, body ->
		out << "<div id='tabs' class=\"vertical-tabs\">"
		out << "<div class='error-panel error hide'><div id='error-icon'></div>${g:message(code:'activity.validation.prompt')}</div>"
		out << verticalTabs(att)
		out << g.formRemote(url:att.url, name:att.name, method:att.method, onSuccess:att.onSuccess) {
			if(att.before) out << body()
			out << wizardTabs(att)
			if(att.after) out << body()
		}
		out << '</div>'
	}

	def verticalTabs = { att ->
		out << '<ul>'
		att.verticalTabs?.split(",")*.trim().eachWithIndex { code, i ->
			def tabName = code.replace('.', '-').toLowerCase();
			out << '<li>'
			out << "<a class=\"tabs-${i+1} tab-${tabName}\" href=\"#tabs-${i+1}\">"
			out << g.message(code:code)
			out << '</a>'
			out << '</li>'
		}
		out << '</ul>'
	}

	def wizardTabs = { att ->
		def tabNames = att.verticalTabs?.replace('.', '-')?.split(",")*.trim()*.toLowerCase()
		att.templates.split(",")*.trim().eachWithIndex { template, i ->
			out << "<div id=\"tabs-${i+1}\" class=\"tab-content-${tabNames?.getAt(i)}\">"
			out << render([template:template])
			out << "</div>"
		}
	}

	def tab = { att, body ->
		def con = att.controller
		out << '<li class="' + con
		if(att.mainNavSection) {
			if (att.mainNavSection == con) out << ' current'
		}
		else if(con == params.controller) {
			out << ' current'
		}
		out << '">'
		out << g.link(controller:con) {
			out << g.message(code:"tab.$con")
			out << body()
		}
		out << '</li>'
	}

	def checkboxGroup = { att,body ->
		if(att.title) out << "<h3>${g.message(code:att.title)}</h3>"
		out << "<div class='input'>"
		if(att.info) out << info([message:att.info])
		out << "<ul class='select ${att.listClass?:''}'>"
		att.values.each { key, checked ->
			def label = g.message(code:att.label + '.' + key)
			def itemAttributes = [checked:checked, name:key, value:true]
			out << '<li><label>'
			out << label
			out << checkbox(itemAttributes)
			out << '</label></li>'
		}
		out << body()
		out << '</ul></div>'
	}

	def radioGroup = { att ->
		def values = att.remove('values')
		values = values instanceof String? values.tokenize(',')*.trim(): values
		def labels = att.labels? att.remove('labels').tokenize(',')*.trim(): null
		def isChecked = { v -> v == att.checked }
		if(att.title) {
			def hTag = att.solo == 'true'? 'h2': 'h3'
			out << "<$hTag>${g.message(code:att.title)}</$hTag>"
		}
		if(att.info) out << info([message:att.info])
		def labelPrefix = att.remove('labelPrefix')?: ''
		def labelSuffix = att.remove('labelSuffix')?: ''
		def descriptionPrefix = att.remove('descriptionPrefix')?: ''
		def descriptionSuffix = att.remove('descriptionSuffix')?: ''
		def hasDescription = descriptionPrefix || descriptionSuffix
		def cssClasses = ['select', 'radio']
		if(!hasDescription) cssClasses << 'no-description'
		out << "<div class='input'>"
		out << "<ul class=\"${cssClasses.join(' ')}\">"
		values.eachWithIndex { value, i ->
			def labelCode = labels? labels[i]: g.message(code:labelPrefix + value + labelSuffix)
			def label = g.message(code:labelCode)
			def id = att.name + '-' + i
			def itemAttributes = att + [value:value, checked:isChecked(value), id:id]
			out << '<li><label>'
			if(hasDescription) {
				out << "<h3>$label</h3>"
				out << info(message:descriptionPrefix + value + descriptionSuffix)
			} else {
				out << label
			}
			out << g.radio(itemAttributes)
			out << '</label></li>'
		}
		out << '</ul></div>'
	}

	/** FIXME use of this taglib should be replaced with CSS white-space:nowrap; */
	def unbroken = { att, body ->
		if(att.value) out << att.value.replaceAll(' ', '&nbsp;')
		if(body) out << body().replaceAll(' ', '&nbsp;')
	}

	def render = { att ->
		boolean rendered = false
		def plugins = grailsApplication.config.frontlinesms.plugins
		def templateId = att.remove 'id'
		def type = att.remove 'type'
		if(type == 'sanchez') {
			def runtimeVars = att.remove('runtimeVars')?.split(",")*.trim()
			if(runtimeVars) {
				if(!att.model) att.model = [:]
				runtimeVars.each { att.model[it] = "{{$it}}" }
			}
		}
		([null] + plugins).each { plugin ->
			if(!rendered) {
				try {
					att.plugin = plugin
					if(type == 'sanchez') out << '<script id="' + templateId + '" type="text/x-sanchez-template">'
					out << g.render(att)
					if(type == 'sanchez') out << '</script>'
					rendered = true
				} catch(GrailsTagException ex) {
					if(ex.message.startsWith("Template not found")) {
						// Thanks for not subclassing your exceptions, guys!
						log.debug "Could not render $plugin:$att.template", ex
					} else {
						throw ex
					}
				}
			}
		}
		if(!rendered) throw new GrailsTagException("Failed to render [att=$att, plugins=${grailsApplication.config.frontlinesms.plugins}]")
	}

	private def templateExists(name, plugin) {
		// FIXME need to use `plugin` variable when checking for resource
		def fullUri = grailsAttributes.getTemplateUri(name, request)
		def resource = grailsAttributes.pagesTemplateEngine.getResourceForUri(fullUri)
		return resource && resource.file && resource.exists()
	}

	def i18nBundle = {
		def locale = RequestContextUtils.getLocale(request)
		// Always include English in case their locale is not available.  The most accurate
		// translation available will take precedence when the JS files are loaded
		grailsApplication.config.frontlinesms.plugins.each { bundle ->
			// TODO this could likely be streamlined by using i18nUtilService.getCurrentLanguage(request)
			['', "_${locale.language}",
					"_${locale.language}_${locale.country}",
					"_${locale.language}_${locale.country}_${locale.variant}"].each { localeSuffix ->
				def link = g.resource plugin:bundle, dir:'i18n', file:"messages${localeSuffix}.js"
				out << "<script type=\"text/javascript\" src=\"$link\" charset=\"UTF-8\"></script>\n" }
		}
	}
	
	def confirmTable = { att ->
		out << '<table id="' + (att.instanceClass.shortName) + '-confirm" class="connection-confirm-table">'
		out << confirmTypeRow(att)
		def fields = getFields(att)
		if(fields instanceof Map) {
			generateConfirmSection(att, fields)
		} else {
			fields.each { out << confirmTableRow(att + [field:it.trim()]) }
		}
		out << '</table>'
	}
	
	def confirmTypeRow = { att ->
		out << '<tr>'
		out << '<td class="field-label">'
		out << g.message(code:"${att.instanceClass.shortName}.type.label")
		out << '</td>'
		out << '<td id="confirm-type"></td>'
		out << '</tr>'
	}
	
	def confirmTableRow = { att ->
		out << '<tr>'
		out << '<td class="field-label">'
		out << getFieldLabel(att.instanceClass, att.field)
		out << '</td>'
		out << '<td id="confirm-' + att.field + '"></td>'
		out << '</tr>'
	}

	def activityConfirmTable = { att, body ->
		out << '<table id="' + att.type + '-confirm" class="activity-confirm-table">'
		out << body()
		def fields = getFields(att)
		fields.each { out << activityConfirmTableRow(att + [field:it.trim()]) }
		out << '</table>'
	}

	def activityConfirmTableRow = { att ->
		out << '<tr>'
		out << '<td class="field-label">'
		out << getActivityFieldLabel(att)
		out << '</td>'
		out << '<td id="confirm-' + att.field + '"></td>'
		out << '</tr>'
	}
	
	def inputs = { att ->
		if(att.table) out << '<table>'
		def fields = getFields(att)
		if(fields instanceof Map) {
			generateSection(att, fields)
		} else {
			def values = att.values
			def types = att.types
			['values', 'types'].each { att.remove(it) }
			fields.eachWithIndex { field, i ->
				def extraAttributes = [field:field]
				if(values) {
					extraAttributes.val = values[i]
					if(types && types[i]) {
						extraAttributes[types[i]] = true
					}
				}
				out << input(att + extraAttributes)
			}
		}
		if(att.submit) {
			if(att.table) out << '<tr><td></td><td>'
			out << g.submitButton(class:'btn', value:g.message(code:att.submit), name:att.submitName?:'submit')
			if(att.table) out << '</td></tr>'
		}
		if(att.table) out << '</table>'
	}
	
	def input = { att, body ->
		def groovyKey = att.field
		// TODO remove references to att.instanceClass and make sure that all forms in app
		// have an instance supplied - whether it is retrieved from the database or created
		// specially for the view
		def instanceClass = att.instance?.getClass()?: att.instanceClass
		def htmlKey = (att.fieldPrefix!=null? att.fieldPrefix: instanceClass?instanceClass.shortName:'') + att.field
		def labelKey = (att.labelPrefix!=null? att.labelPrefix: instanceClass?instanceClass.shortName+'.':'') + att.field + '.label'
		def validationRequired = instanceClass != null
		def val
		if(att.val) {
			val = att.val
		} else if(att.instance) {
			val = att.instance?."$groovyKey"
		} else {
			val = instanceClass?.defaultValues?."$groovyKey"?:null
		}

		['instance', 'instanceClass'].each { att.remove(it) }
		att += [name:htmlKey, value:val]
		if(att.table) out << '<tr><td class="label">'
		else out << '<div class="field">'
		out << '<label for="' + htmlKey + '">'
		out << '' + g.message(code:labelKey)
		if(validationRequired && isRequired(instanceClass, att.field) && !att.isBoolean && !isBooleanField(instanceClass, att.field)) {
			out << '<span class="required-indicator">*</span>'
		}
		out << '</label>'
		if(att.table) out << '</td><td>'
		if(validationRequired) {
			if(att.class) att.class += addValidationCss(instanceClass, att.field)
			else att.class = addValidationCss(instanceClass, att.field)
		}
		
		if(att.password || isPassword(instanceClass, groovyKey)) {	
			out << g.passwordField(att)
		} else if(getMetaClassProperty(instanceClass, groovyKey)?.type?.enum) {
			out << g.select(att + [from:getMetaClassProperty(instanceClass, groovyKey).type.values(),
						noSelection:[null:'- Select -']])
		} else if(att.isBoolean || isBooleanField(instanceClass, groovyKey)) {
			out << g.checkBox(att)
		} else out << g.textField(att)
		out << body()
		if(att.table) {
			out << '</td></tr>'
		} else {
			out << '<div style="clear:both" class="clearfix"></div>'
			out << '</div>'
		}
	}

	def checkBox = { att -> out << checkbox(att) }
	def checkbox = { att ->
		if(att.remove('disabled') in [true, 'disabled']) att.disabled = 'disabled'
		out << g.checkBox(att)
	}

	def magicWand = { att ->
		// edit of activities goes through generic ActivityController, so need to check instance type in this case
		def controller = att.controller == "activity" ? att.instance?.shortName : att.controller
		def target = att.target
		def fields = att.fields ?: expressionProcessorService.findByController(controller)
		def hidden = att.hidden?:false
		target = target?: "messageText"
		def onchange = att.onchange ?: 'magicwand.wave("magicwand-select' + target + '", "' + target + '")'

		out << '<div class="magicwand-container '+ (hidden?'hidden':'') +'">'
		// TODO change this to use g.select if appropriate
		out << "<select id='magicwand-select$target' onchange='${onchange}'>"
		out << '<option value="na" id="magic-wand-na$target" class="not-field">Select option</option>'
		fields.each {
			out << '<option class="predefined-field" value="'+it+'">'
			out << g.message(code:"dynamicfield.${it}.label")
			out << '</option>'
		}
		out << '</select>'
		out << '</div>'
	}

	def recipientSelector = { att ->
		out << '<select name="recipients" id="contactsearch" style="width:320px;" data-placeholder="' + i18nUtilService.getMessage([code:'contact.search.placeholder']) + '" multiple class="chzn-select">'
		out << '<option></option>'
		out << '</select>'
	}

	def unsubstitutedMessageText = { att ->
		out << expressionProcessorService.getUnsubstitutedDisplayText(att.messageText)
	}

	def trafficLightStatus = { att ->
		out << '<span id="status-indicator" class="indicator '
		def connections = Fconnection.list()
		def color = (connections && connections.status.any {(it == ConnectionStatus.CONNECTED)}) ? 'green' : 'red'
		out << color
		out << '"></span>'
	}

	def dateRangePicker = { att ->
		out << datePicker(att + [name:"startDate", value:att.startDate])
		out << datePicker(att + [name:"endDate", value:att.endDate])
	}

	def datePicker = { att ->
		def name = att.name
		def clazz = att.remove('class')
		clazz = clazz? "date-picker $clazz": 'date-picker'
		att.value = att.value ?: 'none'
		att.precision = "day"
		att.noSelection = ['none':'']
		out << "<div class=\"$clazz\">"
		out << g.datePicker(att)
		out << "<input type='hidden' class='datepicker' name='$name-datepicker'/>"
		out << '</div>'
	}

	def quickMessage = { att ->
		att.controller = "quickMessage"
		att.action = "create"
		att.id = "quick_message"
		att.onLoading = "showThinking();"
		// FIXME activity-specific code should not be inside this file
		att.onSuccess = "hideThinking(); mediumPopup.launchMediumWizard(i18n('wizard.quickmessage.title'), data, i18n('wizard.send'), true); mediumPopup.selectSubscriptionGroup(${att.groupId});"
		def body = "<span class='quick-message'>${g.message(code:'fmessage.quickmessage')}</span>"
		out << g.remoteLink(att, body)
	}

	def menu = { att, body ->
		out << '<div id="body-menu" class="'+att.class+'">'
		out << '<ul>'
		out << body()
		out << '</ul>'
		out << '</div>'
	}

	def submenu = { att, body ->
		out << '<li class="'+ att.class +'">'
		if(att.code) {
			out << '<h3>'
			out << g.message(code:att.code)
			out << '</h3>'
		}
		out << '<ul class="submenu">'
		out << body()
		out << '</ul>'
		out << '</li>'
	}

	def menuitem = { att, body ->
		def classlist = att.class?:""
		classlist += att.selected ? " selected" : ""
		out << '<li class="' + classlist + '" >'
		if (att?.bodyOnly)
		{
			out << body()
		}
		else {
			def msg = att.code
			def msgargs = att.msgargs
			def p = att.params
			out << g.link(controller:att.controller, action:att.action, params:p, id:att.id) {
				out << (att.string ? att.string : g.message(code:msg, args:msgargs))
			}
		}
		out << '</li>'
	}

	def select = { att, body ->
		// add the no-selection option to the list if required
		if(!att.hideNoSelection && att.noSelection && att.value != null) {
			def key = (att.noSelection.keySet() as List).first()
			def value = (att.noSelection.values() as List).first()
			if(att.optionKey && att.optionValue) {
				if(att.optionKey && att.optionKey instanceof Closure || att.optionValue instanceof Closure) {
					att.from = [[key:key, value:value]] + att.from
				} else {
					att.from = [[(att.optionKey):key, (att.optionValue):value]] + att.from
				}
			} else {
				if(att.keys) att.keys = [key] + att.keys
				att.from = [value] + att.from
			}
		}
		out << g.select(att, body)
	}
	
	private def getFields(att) {
		def fields = att.remove('fields')
		if(!fields) fields = att.instanceClass?.configFields
		if(fields instanceof String) fields = fields.tokenize(',')*.trim()
		return fields
	}
	
	private def getFieldLabel(clazz, fieldName) {
		g.message(code:"${clazz.shortName}.${fieldName}.label")
	}

	private def getActivityFieldLabel(att) {
		g.message(code:"${att.instanceClass.shortName}.${att.type}.${att.field}.label")
	}
	
	private def isPassword(instanceClass, groovyKey) {
		return getMetaClassProperty(instanceClass, 'passwords') &&
				groovyKey in instanceClass.passwords
	}
	
	private def isBooleanField(instanceClass, groovyKey) {
		return getMetaClassProperty(instanceClass, groovyKey)?.type in [Boolean, boolean]
	}

	private def getMetaClassProperty(clazz, groovyKey) {
		if(clazz) {
			return clazz.metaClass.hasProperty(null, groovyKey)
		}
	}
	
	private def generateSection(att, fields) {
		def keys = fields.keySet()
		keys.each { key ->
			if(fields[key]) {
				out << "<div id=\"$key-subsection\">"
				out << "<fieldset>"
				out << "<legend>"
				out << input(att + [field:key])
				out << "</legend>"
				
				//handle subsections within a subsection
				if(fields[key] instanceof LinkedHashMap) {
					generateSection(att, fields[key])
				} else {
					fields[key].each {field ->
						if(field instanceof String) {
							 out << input(att + [field:field] + [class:"$key-subsection-member"])
						}
					}
				}
				out << "</fieldset>"
				out << "</div>"
			} else {
				out << input(att + [field:key])
			}
			
		}
	}
	
	private def generateConfirmSection(att, fields) {
		def keys = fields.keySet()
		keys.each { key ->
			if(fields[key]) {
				out << "<div class=\"confirm-$key-subsection\">"
				out << confirmTableRow(att + [field:key])
				
				//handle subsections within a subsection
				if(fields[key] instanceof LinkedHashMap) {
					generateConfirmSection(att, fields[key])
				} else {
					fields[key].each {field ->
						if(field instanceof String) {
							 out << confirmTableRow(att + [field:field] + [class:"subsection-member"])
						}
					}
				}
				out << "</div>"
			} else {
				out << confirmTableRow(att + [field:key])
			}
			
		}
	}

	private def isRequired(instanceClass, field) {
		!instanceClass.constraints[field].blank
	}

	private def isInteger(instanceClass, groovyKey) {
		getMetaClassProperty(instanceClass, groovyKey)?.type in [Integer, int]
	}

	private def addValidationCss(instanceClass, field) {
		def cssClasses = ""
		if(isRequired(instanceClass, field) && !isBooleanField(instanceClass, field)) {
			cssClasses += " required "
		}

		if(isInteger(instanceClass, field)) {
			cssClasses += " digits "
		}
		cssClasses
	}
}

