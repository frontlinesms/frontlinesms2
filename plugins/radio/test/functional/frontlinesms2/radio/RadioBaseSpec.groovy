package frontlinesms2.radio

import frontlinesms2.*

class RadioBaseSpec extends geb.spock.GebReportingSpec {
	
	static createRadioShows() {
		[new RadioShow(name:"Morning Show"),
			new RadioShow(name: "Test")].each {it.save(failOnError:true, flush:true)}
		
	}
}