package frontlinesms2

class FconnectionController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def fconnectionService

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [fconnectionInstanceList: Fconnection.list(params), fconnectionInstanceTotal: Fconnection.count()]
    }

    def create = {
        def fconnectionInstance = new Fconnection()
        fconnectionInstance.properties = params
        return [fconnectionInstance: fconnectionInstance]
    }

    def save = {
        def fconnectionInstance = new Fconnection(params)
        if (fconnectionInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
            redirect(action: "show", id: fconnectionInstance.id)
        }
        else {
            render(view: "create", model: [fconnectionInstance: fconnectionInstance])
        }
    }

    def show = {
        def fconnectionInstance = Fconnection.get(params.id)
        if (!fconnectionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
            redirect(action: "list")
        }
        else {
            [fconnectionInstance: fconnectionInstance]
        }
    }

    def edit = {
        def fconnectionInstance = Fconnection.get(params.id)
        if (!fconnectionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [fconnectionInstance: fconnectionInstance]
        }
    }

    def update = {
        def fconnectionInstance = Fconnection.get(params.id)
        if (fconnectionInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (fconnectionInstance.version > version) {
                    
                    fconnectionInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'fconnection.label', default: 'Fconnection')] as Object[], "Another user has updated this Fconnection while you were editing")
                    render(view: "edit", model: [fconnectionInstance: fconnectionInstance])
                    return
                }
            }
            fconnectionInstance.properties = params
            if (!fconnectionInstance.hasErrors() && fconnectionInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
                redirect(action: "show", id: fconnectionInstance.id)
            }
            else {
                render(view: "edit", model: [fconnectionInstance: fconnectionInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def fconnectionInstance = Fconnection.get(params.id)
        if (fconnectionInstance) {
            try {
                fconnectionInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
            redirect(action: "list")
        }
    }
    
    	def createRoute = {
    		withFconnection { settings ->
			fconnectionService.createRoute(settings)
			flash.message = "Created route from ${settings.camelAddress}"
			redirect action:'list'
		}
	}   			
    	def withFconnection(id=params.id, Closure c) {
    		def connection = Fconnection.get(id)
    		if(connection) c.call connection
    		else {
    			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), id])}"
    			redirect action:'list'
    		}
    	}
}

