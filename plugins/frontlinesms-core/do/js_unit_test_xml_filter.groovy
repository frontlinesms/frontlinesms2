#!/usr/bin/env groovy
import groovy.xml.StreamingMarkupBuilder

BufferedReader sin = new BufferedReader(new InputStreamReader(System.in))
def unfiltered = '<unfiltered>' + sin.text + '</unfiltered>'

def slurp = new XmlParser().parseText(unfiltered)
def suites = slurp.testsuites
def toRemove = []
suites.iterator().eachWithIndex { suite, i ->
	if(i % 6 != 0) toRemove << suite
}

toRemove.each { suites.remove(it) }

Node root = new Node(null, 'testsuites')
suites.each { root.append it.children() }

def writer = new StringWriter()
new XmlNodePrinter(new PrintWriter(writer)).print(root)
println '<?xml version="1.0" encoding="UTF-8"?>'
println writer.toString()
