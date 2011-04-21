package frontlinesms2

class ConnectionController {

    def index = {
		def fconnectionInstanceList = Fconnection.findAll()

		return [connectionInstanceList: fconnectionInstanceList]
	}
	def create = {

	}
}
