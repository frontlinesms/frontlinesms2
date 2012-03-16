package frontlinesms2

class Keyword {
	String value
	static belongsTo = [activity: Activity]
	
	static constraints = {
		value(blank: false, nullable: false, maxSize: 255, validator: { val, me ->
			if(val.find(/\s/)) return false
			else {
				if(me.activity.archived) return true
				else {
					def matching = Activity.findByArchivedAndKeyword(false, me)
					return matching == null || matching.id == me.activity.id
				}
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
