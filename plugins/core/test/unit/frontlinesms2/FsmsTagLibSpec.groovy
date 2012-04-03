package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FsmsTagLibSpec extends TagLibSpec {
	def setup() {
		registerMetaClass FsmsTagLib
		FsmsTagLib.metaClass.message = { Map m -> return m.code }
	}
	
	def "input should create div containing label and textfield"() {
		expect:
			// TODO
			false && input([field:'stringfield',
					fieldPrefix:'example-',
					instanceClass:ExampleClass]) == 
				'<div class="field">' + 
				'	<label for="example-stringfield">' +
				'		exampleclass.stringfield.label' +
				'	</label>' +
				'	<input type="text" name="example-stringfield"/>' +
				'</div>'
	}
}

class ExampleClass {
	String stringfield
}
