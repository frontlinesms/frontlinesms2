package frontlinesms2

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class SmartGroupController {
	private static final String CUSTOM_FIELD_ID_PREFIX = 'custom:'
	
	def create = {
		def smartGroupInstance = new SmartGroup()
		def customFieldNames = CustomField.allUniquelyNamed
		[smartGroupInstance:smartGroupInstance,
				fieldNames:['Phone number', 'Contact name', 'email', 'notes']+customFieldNames,
				fieldIds:['mobile', 'contactName', 'email', 'notes']+customFieldNames.collect { CUSTOM_FIELD_ID_PREFIX+it }]
	}
	
	def save = {
		withSmartGroup { smartGroupInstance ->
			smartGroupInstance.name = params.smartgroupname
			if((getRuleText().flatten() - null)) {
				if(params.id) removeSmartGroupRules(smartGroupInstance)
				addSmartGroupRules(smartGroupInstance)
			}
			if(smartGroupInstance.save(flush:true)) {
				flash.message =  message(code: 'flash.smartgroup.saved', args: [smartGroupInstance.name])
				redirect(controller: "contact", action: "show", params:[smartGroupId : smartGroupInstance.id])
			} else {
				flash.error =  message(code: 'flash.smartgroup.save.failed', args: [smartGroupInstance.errors])
				render text: message(code: 'smartgroup.save.failed', args: ['<br/><br/>', params, '<br/><br/>', smartGroupInstance.errors])           	
			}
		}
	}
	
	def show = {
		redirect(controller: "contact", action: "show", params:[smartGroupId:params.id])
	}
	
	def rename = {
		render view: "../smartGroup/rename", model: [groupName: SmartGroup.get(params.groupId)?.name]
	}
	
	def edit = {
		def smartGroupInstance = SmartGroup.get(params.id)
		def smartGroupRuleFields = getSmartGroupRuleFields()
		def currentRules = [:]
		
		for(def prop in smartGroupRuleFields) {
			if(smartGroupInstance."$prop") 
				currentRules."$prop" = smartGroupInstance."$prop"
		}
		def customFieldNames = CustomField.allUniquelyNamed

		render view: "../smartGroup/create", model: [smartGroupInstance:smartGroupInstance,
				currentRules:currentRules,
				fieldNames:[message(code: 'contact.phonenumber.label') , message(code: 'contact.name.label'), message(code: 'contact.email.label'), message(code: 'contact.notes.label')]+customFieldNames,
				fieldIds:['mobile', 'contactName', 'email', 'notes']+customFieldNames.collect { CUSTOM_FIELD_ID_PREFIX+it }]
	}
	
	def confirmDelete = {
		render view: "../group/confirmDelete", model: [groupName: SmartGroup.get(params.groupId)?.name]
	}
	
	def delete = {
		if (SmartGroup.get(params.id)?.delete(flush: true))
				flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'smartgroup.label', default: 'SmartGroup'), ''])}"
		else
			flash.message =  message(code: 'flash.smartgroup.delete.unable')
		redirect(controller: "contact")
	}
	
	private def getRuleText() {
		def t = params['rule-text']
		t instanceof String[] ? t: [t]
	}
	
	private def getRuleField(i) {
		def f = params['rule-field']
		println "field $f"
		if(f instanceof String[]) return f[i]
		else {
			assert i == 0
			return f
		}
	}
	
	private def addSmartGroupRules(smartGroupInstance) {
		getRuleText()?.eachWithIndex { ruleText, i ->
			def ruleField = getRuleField(i)
			if(ruleField.startsWith(CUSTOM_FIELD_ID_PREFIX)) {
				ruleField = ruleField.replaceFirst(CUSTOM_FIELD_ID_PREFIX, '')
				smartGroupInstance.addToCustomFields(new CustomField(name:ruleField, value:ruleText))
			} else {
				assert ruleField in ['contactName', 'mobile', 'email', 'notes'] // prevent injection - these should match the sanctioned fields user can set
				smartGroupInstance."$ruleField" = ruleText
			}
		}
	}
	
	private def removeSmartGroupRules(smartGroupInstance) {
		def smartGroupRuleFields = getSmartGroupRuleFields()
		def fieldsToNullify = smartGroupRuleFields - params['rule-field']
		for(def field in fieldsToNullify) {
			if(field == "customFields") {
				smartGroupInstance.customFields.each {it.smartGroup = null}
				smartGroupInstance.customFields?.clear()
			} else
				smartGroupInstance."$field" = null
		}
	}
	
	private def getSmartGroupRuleFields() {
		def smartGroupRuleFields = (new DefaultGrailsDomainClass(SmartGroup.class)).persistentProperties*.name - "name"
		smartGroupRuleFields
	}
	
	private def withSmartGroup(Closure c) {
		def sg
		if(params.id) sg = SmartGroup.get(params.id)
		else sg = new SmartGroup()
		if(sg) {
			c.call(sg)
		}
		else render(text: message(code: 'smartgroup.id.exist.not', args: [params.id]))
	}
}
