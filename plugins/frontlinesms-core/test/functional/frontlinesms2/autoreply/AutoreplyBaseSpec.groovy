package frontlinesms2.autoreply

import frontlinesms2.*

class AutoreplyBaseSpec extends grails.plugin.geb.GebSpec {

	static createTestAutoreply(){
		def a = new Autoreply(name:"Fruits", autoreplyText:"Hello")
			a.addToKeywords(new Keyword(value:"MANGO"))
			a.addToKeywords(new Keyword(value:"ORANGE"))
			a.save(flush:true, failOnError:true)
	}
}