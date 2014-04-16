package frontlinesms2

import org.springframework.web.servlet.support.RequestContextUtils
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import frontlinesms2.CoreAppInfoProviders as CAIP
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator

import org.apache.commons.math.random.RandomDataImpl;

class FsmsTagLib {
	static namespace = 'fsms'

	def appSettingsService
	def expressionProcessorService
	def grailsApplication
	def i18nUtilService
	def statusIndicatorService
	GrailsConventionGroovyPageLocator groovyPageLocator

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
		if(att.selectedOverride) {
			if (att.selectedOverride) out << ' current'
		}
		else if(att.mainNavSection) {
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
			att.remove('title')
		}
		if(att.info) out << info([message:att.info])
		def labelPrefix = att.remove('labelPrefix')?: ''
		def labelSuffix = att.remove('labelSuffix')?: ''
		def descriptionPrefix = att.remove('descriptionPrefix')?: ''
		def descriptionSuffix = att.remove('descriptionSuffix')?: ''
		def hasDescription = descriptionPrefix || descriptionSuffix
		def useImages = att.remove('useImages')
		def cssClasses = ['select', 'radio']
		if(!hasDescription) cssClasses << 'no-description'
		out << "<div class='input'>"
		out << "<ul class=\"${cssClasses.join(' ')}\">"
		values.eachWithIndex { value, i ->
			def labelCode = labels? labels[i]: g.message(code:labelPrefix + value + labelSuffix)
			def label = g.message(code:labelCode)
			def id = att.name + '-' + i
			def itemAttributes = att + [value:value, checked:isChecked(value), id:id]
			if(att.disabledValues && att.disabledValues.split(',').contains(value)) {
				itemAttributes += [disabled: 'disabled']
				out << '<li class="disabled"><label>'
			}
			else {
				out << '<li><label>'
			}
			def imagePath = "images/icons/${value}.png"
			if(hasDescription) {
				if(useImages && grailsApplication.parentContext.getResource(imagePath)?.exists()) {
					out << "<img src='${g.resource(file: imagePath)}' />"
				}
				else
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
				} catch(Exception ex) {
					if((ex instanceof GrailsTagException && ex.message.startsWith('Template not found')) ||
							(ex instanceof IllegalArgumentException && ex.message == 'Argument [txt] cannot be null or blank')) {
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

	def templateElseBody = { att, body ->
		try {
			out << render(att)
		}
		catch(GrailsTagException gte) {
			out << body()
		}
	}

	def interactionTemplate = { att, body ->
		def interactionType = (controllerName == 'missedCall' ? 'missedCall' : 'message')
		def requestedTemplate = att.template
		// TODO find a way to reuse templateElseBody here - this re-implementation is to work around fact that 2nd render would be
		// rendered before passing to templateElseBody, which could cause failure
		try {
			out << render(att + [template: "/$interactionType/$requestedTemplate"])
		}
		catch(GrailsTagException gte) {
			out << render(att + [template: "/interaction/$requestedTemplate"])
		}
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
				if(i18nUtilService.allTranslations.containsKey(localeSuffix - '_')) {
					def link = g.resource plugin:bundle, dir:'i18n', file:"messages${localeSuffix}.js"
					out << "<script type=\"text/javascript\" src=\"$link\" charset=\"UTF-8\"></script>\n"
				}
			}
		}
	}

	def confirmTable = { att ->
		def fields = getFields(att)
		if (fields) {
			out << '<table id="' + (att.instanceClass.shortName) + '-confirm" class="connection-confirm-table">'
			out << confirmTypeRow(att)
			if(fields instanceof Map) {
				generateConfirmSection(att, fields)
			} else {
				fields.each { out << confirmTableRow(att + [field:it.trim()]) }
			}
			out << '</table>'
		}
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
		if(att.list) out << "<div class='field-list'>"
		def fields = getFields(att)
		if(!hasCustomConfigTemplate(att)) {
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
				if(att.list) out << "<div class='input-item'>"
				out << g.submitButton(class:'btn', value:g.message(code:att.submit), name:att.submitName?:'submit')
				if(att.list) out << '</div>'
				if(att.table) out << '</td></tr>'
			}
		} else {
			out << render(template: "/fconnection/${att.instanceClass?.shortName}/config")
		}
		if(att.table) out << '</table>'
		if(att.list) out << '</div>'
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

		if(att.list) out << "<div class='input-item'>"
		else if(att.table) out << '<tr><td class="label">'
		else out << '<div class="field">'
		if((att.instanceClass?.configFields && !groovyKey.startsWith("info-")) || (!att.instanceClass?.configFields && att.field)) {
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
		} else if(att.instanceClass?.configFields) {
			out << "<div class='connection-info'>${g.message(code:"${att.instance?.class?.shortName?:instanceClass?.shortName?:'connection'}.${groovyKey}").markdownToHtml()}</div>"
		}
		if(att.list) out << "</div>"
		else if(att.table) {
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
		if (att.explanatoryText) {
			out << '<div class="recipient_selector_wrap"><h2>'
			out << i18nUtilService.getMessage([code:'contact.search.helptext'])
			out << '</h2></div>'
		}
		out << '<select name="recipients" style="width:320px;" data-placeholder="' + i18nUtilService.getMessage([code:'contact.search.placeholder']) + '" multiple class="chzn-select customactivity-field">'

		def contacts = att?.contacts
		def groups = att?.groups
		def smartGroups = att?.smartGroups
		def addresses = att?.addresses

		["contact":contacts, "group":groups, "smartgroup":smartGroups, "address":addresses].each { typeKey, typeValue ->
			if ( typeValue ) {
				def optgroupLabel, optionValue, optionLabel
				optgroupLabel = g.message(code:'contact.search.'+typeKey)
				out << "<optgroup label='${optgroupLabel}'>"
					typeValue.each { recipient ->
						optionValue = typeKey + '-'
						if (typeKey == 'address') {
							optionValue += recipient
							optionLabel = recipient.toPrettyPhoneNumber()
						} else {
							optionValue += recipient.id
							optionLabel = recipient.name
						}
						out << "<option value='${optionValue}' selected>${optionLabel}</option>"
					}
				out << '</optgroup>'
			}
		}
		out << '</select>'
	}

	def unsubstitutedMessageText = { att ->
		out << expressionProcessorService.getUnsubstitutedDisplayText(att.messageText)
	}

	def trafficLightStatus = { att ->
		out << '<span id="status-indicator" class="indicator '
		def connections = Fconnection.list()
		out << statusIndicatorService.color
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
		def popupCall = "mediumPopup.launchMediumWizard(i18n('wizard.quickmessage.title'), data, i18n('wizard.send'), true);"
		def params = [ groupList:(att.groupList?:'') ]
		att << [controller:'quickMessage', action:'create', id:'quick_message', popupCall:popupCall, params:params]
		def body = "<span class='quick-message'>${g.message(code:'fmessage.quickmessage')}</span>"
		out << fsms.popup(att, body)
	}

	def popup = { att, body ->
		att << [onLoading:"showThinking();", onSuccess:"hideThinking(); ${att.popupCall}"]
		att.remove('popupCall')
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
		def icon
		def iconMap = ['messages': 'envelope', 'missedCalls': 'phone', 'activities': 'comments', 'folders':'folder-open']
		if(att.class in iconMap.keySet()) {
			icon = iconMap[att.class]	
		}
		out << '<li class="'+ att.class +'">'
		if(att.code) {
			out << '<h3>'
			if(icon) {
				out << "<i class='menuIcon icon-$icon'></i>"
			}
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
		out << '<li class="' + classlist + '" '
		if(att?.entitytype)
			out << "entitytype='${att.entitytype}' "
		if(att?.entityid)
			out << "entityid='${att.entityid}' "
		out << '>'
		if (att?.bodyOnly)
		{
			out << body()
		}
		else {
			def msg = att.code
			def msgargs = att.msgargs
			def p = att.params
			out << g.link(controller:att.controller, action:att.action, params:p, id:att.id) {
				out << "<span class='menu-item-label'>${(att.string ? att.string : g.message(code:msg, args:msgargs))}</span>"
				if (body) {
					out << body()
				}
			}
		}
		out << '</li>'
	}

	def unreadCount = { att, body ->
		def val = att.unreadCount
		out << "<span class='unread_message_count ${val == 0 ? 'zero' : ''}'>" + val + "</span>"
	}

	def pendingCount = { att, body ->
		def val = att.pendingCount
		out << "<span class='pending_message_count ${val == 0 ? 'zero' : ''}'>" + val + "</span>"
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

	def messageComposer = { att, body ->
		out << '<div class="message-composer">'
			//<fsms:messageComposer name="myMessage" placeholder="message.compose.prompt"/>
			def placeholder = g.message(code:att.placeholder)
			out << g.textArea(
				name:att.name,
				value:att.value,
				placeholder:placeholder,
				rows:att.rows?:"3",
				id:att.textAreaId)

			out << '<div class="controls">'
				out << '<div class="character-count-display">0</div><br/>'
				def magicWandAttributes = [controller:att.controller, target:att.target, fields:att.fields, hidden:false]
				out << magicWand(magicWandAttributes)
			out << '</div>'
			out << '<div class="clearfix"></div>'

		out << '</div>'
	}

	def step = { att, body ->
		out << render(template:'/customactivity/step', model:[stepId:att.stepId, type:att.type, body:body])
	}

	def fieldErrors = { att, body ->
		def errors = att.bean?.errors?.allErrors.findAll{ it.field == att.field }
		def errorMessages = errors.collect { message(error:it) }.join(att.delimeter?:" ")
		if (errors && errorMessages) {
			out << "<label for='${att.field}' generated='true' class='error'>"
			out << errorMessages
			out << "</label>"
		}
	}

	def contactWarning = { att, body ->
		def warningType = att.warningType
		out << "<div for='mobile' class='warning-display warning ${warningType}' style='display:none'>"
		out << "<a onclick='contactEditor.dismissWarning(\"${warningType}\")' name='dismiss${warningType}' id='dismiss${warningType}' class='dismissWarning' title='${g.message(code:'contact.phonenumber.' + warningType + '.dismiss').encodeAsHTML()}''>"
		out << "<i class='icon-remove'></i>"
		out << "</a>"
		def header = g.message(code: "contact.phonenumber." + warningType + ".header")
		def description = g.message(code: "contact.phonenumber." + warningType + ".description")
		if(warningType != "NonNumericNotAllowedWarning") {
			out << "<p><em>${header}</em></p>"
		}
		out << "<p>${description}</p>"
		out << "</div>"
	}

	def frontlineSyncPasscode = { att, body ->
		def connection = att.connection
		def passcode
		if(connection) {
			passcode = connection.secret
		} else {
			def randomData = new RandomDataImpl()
			passcode = randomData.nextInt(1000, 9999)
		}
		out << g.hiddenField(name:'frontlinesyncsecret', value:passcode)
		out << "<div class='passcode-pretty'>${passcode}</div>"
	}

	private def getFields(att) {
		def fields = att.remove('fields')
		if(!fields) fields = att.instanceClass?.configFields
		if(fields instanceof String) fields = fields.tokenize(',')*.trim()
		return fields
	}

	private def hasCustomConfigTemplate(att) {
		return (groovyPageLocator.findTemplateByPath("/fconnection/${att.instanceClass?.shortName}/config") != null)
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
				if(att.list) out << "<fieldset class='table'>"
				else out << "<fieldset>"
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
				if(!key.startsWith("info-")) out << confirmTableRow(att + [field:key])

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

