package frontlinesms2

class FsmsTagLib {
	static namespace = 'fsms'

	def i18n = { att ->
		out << '<script type="text/javascript">'
		out << "	var i18nStrings = {};\n"
		att.keys.tokenize(',')*.trim().each {
			def propVal = g.message(code:it)
			propVal = propVal.replaceAll("\\'", "\\\\'")
			out << "	i18nStrings['$it'] = '${propVal}';\n"
		}
		out << '</script>'
	}
	
	def confirmTable = { att ->
		out << '<table id="' + (att.instanceClass.simpleName.toLowerCase() - 'fconnection') + '-confirm">'
		out << confirmTypeRow(att)
		getFields(att).each {
			out << confirmTableRow(att + [field:it.trim()])
		}
		out << '</table>'
	}
	
	def confirmTypeRow = {att ->
		out << '<tr>'
		out << '	<td class="bold">'
		out << g.message(code:"${att.instanceClass.simpleName.toLowerCase()}.type.label")
		out << '  </td>'
		out << '	<td id="confirm-type"></td>'
		out << '</tr>'
	}
	
	def confirmTableRow = { att ->
		out << '<tr>'
		out << '	<td class="bold">'
		out << getFieldLabel(att.instanceClass, att.field)
		out << '  </td>'
		out << '	<td id="confirm-' + att.field + '"></td>'
		out << '</tr>'
	}
	
	def inputs = { att ->
		def fields = getFields(att)
		println "FsmsTagLib.input() : fields=$fields"
		fields.each {
			out << input(att + [field:it])
		}
	}
	
	def input = { att ->
		def groovyKey = att.field
		def htmlKey = (att.fieldPrefix?:att.instanceClass?att.instanceClass.shortName:'') + att.field
		def val = att.instance?."$groovyKey"
		def instanceClass = att.instance?.getClass()?: att.instanceClass
		
		['instance', 'instanceClass'].each { att.remove(it) }
		att += [name:htmlKey, value:val]
		
		out << '<div class="field">'
		out << '	<label for="' + htmlKey + '">'
		out << '		' + getFieldLabel(instanceClass, groovyKey)
		out << '	</label>'
		
		if(att.password || isPassword(instanceClass, groovyKey)) {
			out << g.passwordField(att)
		} else if(instanceClass.metaClass.hasProperty(null, groovyKey)?.type.enum) {
			out << g.select(att + [from:instanceClass.metaClass.hasProperty(null, groovyKey).type.values(),
						noSelection:[null:'- Select -']])
		} else out << g.textField(att)
		out << '</div>'
	}
	
	private def getFields(att) {
		def fields = att.remove('fields')
		if(!fields) fields = att.instanceClass?.configFields
		if(fields instanceof String) fields = fields.tokenize(',')*.trim()
		return fields
	}
	
	private def getFieldLabel(clazz, fieldName) {
		g.message(code:"${clazz.simpleName.toLowerCase()}.${fieldName}.label")
	}
	
	private def isPassword(instanceClass, groovyKey) {
		return instanceClass.metaClass.hasProperty(null, 'passwords') &&
				groovyKey in instanceClass.passwords
	}
}
