package frontlinesms2

class Keyword {
	String value
	static belongsTo = [activity: Activity]
	
	static constraints = {
		value(blank:true, maxSize:255, validator: { val, me ->
			if(val.find(/\s/)) return false
			if(val != val.toUpperCase()) return false
			if(me.activity?.deleted || me.activity?.archived) return true
			def found = Keyword.findAllByValue(val)
			if(!found || found==[me]) return true
			else if (found.any { it != me && !it.activity?.archived && !it.activity?.deleted }) return false
			else return true
		})
		activity(nullable: false)
	}
    
	def beforeSave = {
		value = value?.trim()?.toUpperCase()
	}
	
	def beforeUpdate = beforeSave
	def beforeInsert = beforeSave
}
