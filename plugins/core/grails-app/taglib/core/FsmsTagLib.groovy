package core

class FsmsTagLib {
	static namespace = 'fsms'
	
	def confirmTable = { att ->
		out << '<table id="' + (att.instanceClass.simpleName.toLowerCase() - 'fconnection') + '-confirm">'
		def fields = att.remove('fields').tokenize(',')
		fields.each {
			out << confirmTableRow(att + [field:it.trim()])
		}
		out << '</table>'
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
		def fields = att.fields
		att.remove('fields')
		println "FsmsTagLib.input() : fields=$fields"
		fields.tokenize(',').each {
			out << input(att + [field:it.trim()])
		}
	}
	
	def input = { att ->
		def groovyKey = att.field
		def htmlKey = (att.fieldPrefix?:'') + att.field
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
	
	private def getFieldLabel(clazz, fieldName) {
		g.message(code:"${clazz.simpleName.toLowerCase()}.${fieldName}.label")
	}
	
	private def isPassword(instanceClass, groovyKey) {
		return instanceClass.metaClass.hasProperty(null, 'passwords') &&
				groovyKey in instanceClass.passwords
	}
}
