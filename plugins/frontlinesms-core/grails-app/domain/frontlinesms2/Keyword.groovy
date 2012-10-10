package frontlinesms2

class Keyword {
	String value
	static belongsTo = [activity: Activity]
	String ownerDetail
	boolean isTopLevel

	static mapping = {
		version false
	}
	
	static constraints = {
		value(blank:true, maxSize:255, validator: { val, me ->
			if(val.find(/\s/)) return false
			if(val != val.toUpperCase()) return false
			if(me.activity?.deleted || me.activity?.archived) return true
			def found
			if(me.isTopLevel)
				found = Keyword.findAllByValueAndIsTopLevel(val, true)
			else
				found = Keyword.findAllByValueAndActivityAndIsTopLevel(val, me.activity, false)
			if(!found || found==[me]) return true
			else if (found.any { it != me && !it.activity?.archived && !it.activity?.deleted }) return false
			else return true
			if(!found || found==[me]) return true
		})
		activity(nullable: false)
		ownerDetail(nullable: true, validator: {val, me ->
			me.isTopLevel || val
		})
	}

	static namedQueries = {
		matchFirstLevel { word ->
			activity {
				eq('archived', false)
				eq('deleted', false)
			}
			eq('value', word.toUpperCase())
		}
		matchSecondLevel { word -> //TODO
			activity {
				eq('archived', false)
				eq('deleted', false)
			}
			eq('value', word.toUpperCase())
		}
	}

	static Keyword getMatch(String word) {
		def list = Keyword.match(word).list()
		return list? list[0]: null
	}
    
	def beforeSave = {
		value = value?.trim()?.toUpperCase()
	}
	
	def beforeUpdate = beforeSave
	def beforeInsert = beforeSave
}

