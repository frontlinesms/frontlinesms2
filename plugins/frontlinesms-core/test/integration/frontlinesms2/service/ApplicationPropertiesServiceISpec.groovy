package frontlinesms2.service

import spock.lang.*

import frontlinesms2.*

class ApplicationPropertiesServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def applicationPropertiesService

	def cleanup(){
		new File(System.getProperty("user.home") +'/something.properties').delete()
	}
	def 'given property file with specified values, service actions should return correct values'(){
		setup:
			def fileLocation = System.getProperty("user.home") +'/something.properties'
			def propFile = new File(fileLocation).append("last.version.run=2.2.0\nshow.new.features.popup=true\nlast.version.popup.already.displayed=false")
		when:
			applicationPropertiesService.propertyFileLocation = fileLocation
		then:
			applicationPropertiesService.lastVersionRun == "2.2.0"
			applicationPropertiesService.showNewFeaturesPopup == true
			applicationPropertiesService.lastVersionPopupAlreadyDisplayed == false
	}

	def 'setting the service properties should persist them in the stated properties file'(){
		setup:
			def fileLocation = System.getProperty("user.home") +'/something.properties'
			def propFile = new File(fileLocation).append("last.version.run=2.2.0\nshow.new.features.popup=true\nlast.version.popup.already.displayed=false")
		when:
			applicationPropertiesService.propertyFileLocation = fileLocation
			applicationPropertiesService.lastVersionRun = "2.2.7"
			applicationPropertiesService.showNewFeaturesPopup = false
			applicationPropertiesService.lastVersionPopupAlreadyDisplayed = true
		then:
			applicationPropertiesService.lastVersionRun == "2.2.7"
			applicationPropertiesService.showNewFeaturesPopup == false
			applicationPropertiesService.lastVersionPopupAlreadyDisplayed == true
	}
}