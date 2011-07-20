package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat

class StatusController {
	def index = {
		fetchAllStatus()
	}

	def trafficLightIndicator = {
		render text:getStatus(fetchAllStatus()).getIndicator(), contentType:'text/plain'
	}
	
	private def getStatus(allStatus) {
		if(fetchAllStatus().any { it.value == ConnectionStatus.NOT_CONNECTED}) return ConnectionStatus.NOT_CONNECTED
		else if(fetchAllStatus().any { it.value ==  ConnectionStatus.ERROR}) return ConnectionStatus.ERROR
		else return ConnectionStatus.CONNECTED
	}

	//FIXME: This is a stub method.
	private def fetchAllStatus() {
		['MTNDONGLE' : ConnectionStatus.ERROR, "GMAIL": ConnectionStatus.CONNECTED, "INTERNET": ConnectionStatus.CONNECTED,
			"MESSAGEQUEUE": ConnectionStatus.CONNECTED]
	}
}