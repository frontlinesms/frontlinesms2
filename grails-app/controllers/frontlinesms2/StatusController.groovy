package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat

class StatusController {
	final def RED = 'red'
	final def ORANGE = 'orange'
	final def GREEN = 'green'

	def index = {
		Fconnection.fetchAllStatus()
	}

	def trafficLightIndicator = {
		def groupedStatus = Fconnection.fetchAllStatus().groupBy{it.value}
		def statusIndicator = groupedStatus[ConnectionStatus.CONNECTED] ? RED : (groupedStatus[ConnectionStatus.ERROR] ? ORANGE : GREEN)
  		render text:statusIndicator, contentType:'text/plain'
	}
}