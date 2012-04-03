package frontlinesms2

class Keyword {
	String value
	static belongsTo = [activity: Activity]
	
	static constraints = {
		value(blank:true, nullable:false, maxSize:255, validator: { val, me ->
			if(!(val ==~ /[A-Z]*/)) return false
			else {
				if(me.activity?.archived) return true
				def found = Keyword.findAllByValue(val)
				println "Found: $found"
				if(!found || found==[me]) return true
				else if (found.any { it != me && !it.activity?.archived }) {
					println "Returning false because ?"
					return false
				}
				else return true
			}
		})
		activity(nullable: false)
	}
    
	def beforeSave = {
		value = value?.trim()?.toUpperCase()
	}
	
	def beforeUpdate = beforeSave
	def beforeInsert = beforeSave
}
