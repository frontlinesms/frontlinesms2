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
		fields.tokenize(',').each {
			out << input(att + [field:it.trim()])
		}
	}
	
	def input = { att ->
		def key = att.field
		def val = att.instance?."$key"
		def instanceClass = att.instance?.getClass()?: att.instanceClass
		println "Attributes: $att"
		println "InstanceClass: $instanceClass"
		
		['instance', 'instanceClass'].each { att.remove(it) }
		att += [name:key, value:val]
		
		out << '<div class="field">'
		out << '	<label for="' + key + '">'
		out << '		' + getFieldLabel(instanceClass, key)
		out << '	</label>'
		if(att.password || instanceClass.passwords?.contains(field)) {
			out << g.passwordField(att)
		} else if(instanceClass.metaClass.hasProperty(null, key)?.type.enum) {
			println "input() : isEnum() : key: $key"
			println "input() : isEnum() : type: ${instanceClass.metaClass.hasProperty(null, key)?.type}"
			println "input() : isEnum() : values: ${instanceClass.metaClass.hasProperty(null, key)?.type.values()}"
			out << g.select(att + [from:instanceClass.metaClass.hasProperty(null, key).type.values(),
						noSelection:[null:'- Select -']])
		} else out << g.textField(att)
		out << '</div>'
	}
	
	private def getFieldLabel(clazz, fieldName) {
		g.message(code:"${clazz.simpleName.toLowerCase()}.${fieldName}.label")
	}
}
