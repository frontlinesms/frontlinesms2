package frontlinesms2.camel

import spock.lang.Specification

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.spi.UnitOfWork
import org.apache.camel.spi.RouteContext
import org.apache.camel.model.RouteDefinition

class CamelUnitSpecification extends Specification {
	def mockDispatchMessage(String messageText) {
		def m = Mock(Message)
		m.body >> [
			id:'45678',
			message:[
				text:messageText,
				toString:{"mock Fmessage"}
			],
			text:messageText,
			dst:'+1234567890',
			toString:{"mock body (Dispatch)"}
		]
		return m
	}
	
	Exchange mockExchange(messageText) {
		def inMessage = mockDispatchMessage(messageText)
		def x = Mock(Exchange)
		def route = Mock(RouteDefinition)
		route.id >> "route-1"
		def routeContext = Mock(RouteContext)
		routeContext.route >> route
		def unitOfWork = Mock(UnitOfWork)
		unitOfWork.routeContext >> routeContext
		x.unitOfWork >> unitOfWork
		x.in >> inMessage
		def out = Mock(Message)
		out.headers >> [:]
		x.out >> out
		println "mockExchange() : x.out=$x.out"
		return x
	}
}

