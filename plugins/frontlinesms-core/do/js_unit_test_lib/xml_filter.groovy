#!/usr/bin/env groovy
import groovy.xml.StreamingMarkupBuilder

BufferedReader sin = new BufferedReader(new InputStreamReader(System.in))
def unfiltered = '<unfiltered>' + sin.text + '</unfiltered>'

def slurp = new XmlParser().parseText(unfiltered)
def suites = slurp.testsuites
suites.retainAll { suites.lastIndexOf(it) == suites.size() - 1 }

Node root = new Node(null, 'testsuites')
suites.each { testsuites ->
	testsuites.children().each { testsuite ->
		def expectedChildren = ['properties', 'system-out', 'system-err']
		def classname = 'js.' + testsuite.attributes().name
		testsuite.attributes().name = classname
		testsuite.children().each { testcase ->
			expectedChildren.remove(testcase.name())
			if(testcase.name() == 'testcase') {
				def att = testcase.attributes()
				['total', 'failed'].each { att.remove(it) }
				att.classname = classname
			}
		}
		expectedChildren.each { name ->
			testsuite.appendNode(name)
		}
		root.append testsuite
	}
}

def writer = new StringWriter()
new XmlNodePrinter(new PrintWriter(writer)).print(root)
println '<?xml version="1.0" encoding="UTF-8"?>'
println writer.toString()

