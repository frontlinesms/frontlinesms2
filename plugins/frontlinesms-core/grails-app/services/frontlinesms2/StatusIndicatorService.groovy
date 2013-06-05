package frontlinesms2

import frontlinesms2.ConnectionStatus as CS

class StatusIndicatorService {
	def getColor() {
		def status = Fconnection.list()*.status
		if(CS.CONNECTED in status) {
			return 'green'
		} else if(CS.CONNECTING in status) {
			return 'orange'
		} else if(CS.FAILED in status) {
			return 'red'
		}
		return 'grey'
	}
}
