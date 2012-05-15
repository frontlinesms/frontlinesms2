package frontlinesms2

class MagicWandService {
	static transactional = true	
	def fields = [
	    'contact_name' : ['quickCompose', 'reply', 'reply_all'],
	    'contact_number' : ['quickCompose', 'poll', 'auto_forward'],
	    'message_content' : ['poll', 'announcement'],
	    'keyword' : ['poll', 'announcement']]

	def findByView(viewName) {
	    def ret = [:]
	    fields.each {
	        def active = false
	        it.value.each { i ->
	            if (i == viewName)
	            {
	                active = true
	            }
	        }
	        ret.put(it.key, active)
	    }
	    ret
	}


}