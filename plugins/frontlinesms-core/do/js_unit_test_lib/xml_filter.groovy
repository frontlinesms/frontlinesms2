#!/usr/bin/env groovy
import groovy.xml.StreamingMarkupBuilder

BufferedReader sin = new BufferedReader(new InputStreamReader(System.in))
def unfiltered = '<unfiltered>' + sin.text + '</unfiltered>'

def slurp = new XmlParser().parseText(unfiltered)
def suites = slurp.testsuites

Node root = new Node(null, 'testsuites')
// Reverse the suites so we get the last-run first - this should give us proper timings for
// the tests
def alreadySeen = []
suites.reverse().each { testsuites ->
	testsuites.children().each { testsuite ->
		def classname = 'js.' + testsuite.attributes().name
		if(classname in alreadySeen) return;
		alreadySeen << classname
		def expectedChildren = ['properties', 'system-out', 'system-err']
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

