package core

import spock.lang.*
import grails.plugin.spock.*

class FsmsTagLibSpec extends TagLibSpec {
	def setup() {
		// TODO mock messages
	}
	
	def "input should create div containing label and textfield"() {
		expect:
			input([field:'stringfield',
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