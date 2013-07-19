package grails.plugin.geb

import grails.plugin.remotecontrol.RemoteControl
import groovy.sql.Sql

class GebSpec extends geb.spock.GebReportingSpec {
	static final remoteControl
	static {
		remoteControl = new RemoteControl()
		geb.Page.metaClass.static.remote = { Closure c -> remoteControl.exec(c) }

		injectNavigatorMethods()
	}
	static remote(Closure c) { remoteControl.exec(c) }

	def cleanupSpec() {
		remote {
			// CLearing the hibernate session should improve performance of tests over time
			frontlinesms2.Contact.withSession { s ->
				s.clear()
			}
			null
		}
	}

	def cleanup() {
		remote {
			def appInstanceId = System.properties['frontlinesms.appInstanceId']
			if(!appInstanceId) appInstanceId = "${new Random().nextLong()}"
			System.properties['frontlinesms.appInstanceId'] = appInstanceId

			def sql = Sql.newInstance("jdbc:h2:mem:testDb$appInstanceId", 'sa', '', 'org.h2.Driver')
			sql.execute "SET REFERENTIAL_INTEGRITY FALSE"
			sql.eachRow("SHOW TABLES") { table -> sql.execute('DELETE FROM ' + table.TABLE_NAME) } 
			sql.execute "SET REFERENTIAL_INTEGRITY TRUE"
			null
		}
	}

	private static injectNavigatorMethods() {
		// N.B. this setup uses undocumented features of Geb which could be subject
		// to unnanounced changes in the future - take care when upgrading Geb!
		def emptyMc = new geb.navigator.AttributeAccessingMetaClass(new ExpandoMetaClass(geb.navigator.EmptyNavigator))
		def nonEmptyMc = new geb.navigator.AttributeAccessingMetaClass(new ExpandoMetaClass(geb.navigator.NonEmptyNavigator))

		def serverAddress = remote {
			def servletContext = ctx.servletContext
			def grailsApplication = app

			final String contextPath = servletContext.contextPath
			// FIXME in grails 2, serverURL appears to be not set, so hard-coding it here
			//final String baseUrl = grailsApplication.config.grails.serverURL
			final String serverPort = grailsApplication.config.grails.serverPort?:System.properties['server.port']?: '8080'
			final String baseUrl = "http://localhost:${serverPort}${contextPath}"
			return [baseUrl:baseUrl, contextPath:contextPath]
		}
		def baseUrl = serverAddress.baseUrl
		def contextPath = serverAddress.contextPath

		nonEmptyMc.'get@href' = {
			def val = getAttribute('href')
			if(val.startsWith(contextPath)) val = val.substring(contextPath.size())
			// check for baseUrl second, as it includes the context path
			else if(val.startsWith(baseUrl)) val = val.substring(baseUrl.size())
			return val
		}

				/** list of boolean vars from https://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/WebElement.html#getAttribute(java.lang.String) */
		final def BOOLEAN_PROPERTIES = ['async', 'autofocus', 'autoplay', 'checked', 'compact', 'complete', 'controls', 'declare', 'defaultchecked', 'defaultselected', 'defer', 'disabled', 'draggable', 'ended', 'formnovalidate', 'hidden', 'indeterminate', 'iscontenteditable', 'ismap', 'itemscope', 'loop', 'multiple', 'muted', 'nohref', 'noresize', 'noshade', 'novalidate', 'nowrap', 'open', 'paused', 'pubdate', 'readonly', 'required', 'reversed', 'scoped', 'seamless', 'seeking', 'selected', 'spellcheck', 'truespeed', 'willvalidate']
		BOOLEAN_PROPERTIES.each { name ->
			def getterName = "is${name.capitalize()}"
			emptyMc."$getterName" = { false }
			nonEmptyMc."$getterName" = {
				def v = delegate.getAttribute(name)
				!(v == null || v.length()==0 || v.equalsIgnoreCase('false'))
			}
		}

		def oldMethod = nonEmptyMc.getMetaMethod("setInputValue", [Object] as Class[])
		nonEmptyMc.setInputValue = { value ->
			if(input.tagName == 'selected') {
				throw new RuntimeException("OMG youre playing with selecters!")
			} else {
				oldMethod.invoke(value)
			}
		}

		emptyMc.initialize()
		geb.navigator.EmptyNavigator.metaClass = emptyMc
		nonEmptyMc.initialize()
		geb.navigator.NonEmptyNavigator.metaClass = nonEmptyMc
	}
}

