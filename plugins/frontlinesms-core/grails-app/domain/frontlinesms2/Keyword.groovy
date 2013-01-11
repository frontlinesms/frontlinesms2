package frontlinesms2

class Keyword {
	String value
	static belongsTo = [activity: Activity]
	String ownerDetail
	boolean isTopLevel = true

	static mapping = {
		version false
	}
	
	static constraints = {
		value(blank:true, maxSize:255, validator: { val, me ->
			println "## Checking the Keywords for ${me.activity} ##"
			if(val.find(/\s/)) return false
			if(val != val.toUpperCase()) return false
			if(me.activity?.deleted || me.activity?.archived) return true
			def found = Keyword.findAllByValueAndIsTopLevel(val, me.isTopLevel)
			if(!found || found==[me]) return true
			else if (found.any { 
				if (me.isTopLevel)
					return it != me && !it.activity?.archived && !it.activity?.deleted
				else
					return it != me && it.activity == me.activity
			}) return false
			else return true
		})
		activity(nullable: false)
		ownerDetail(nullable: true, validator: {val, me ->
			me.isTopLevel || val
		})
	}

	static namedQueries = {
		matchFirstLevel { word ->
			eq('isTopLevel', true)
			activity {
				eq('archived', false)
				eq('deleted', false)
			}
			eq('value', word.toUpperCase())
		}
		matchSecondLevel { word, act ->
			eq('isTopLevel', false)
			eq('activity', act)
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

	static Keyword getFirstLevelMatch(String word) {
		def list = Keyword.matchFirstLevel(word).list()
		return list? list[0]: null
	}

	static Keyword getSecondLevelMatchInActivity(String word, Activity act) {
		def list = Keyword.matchSecondLevel(word, act).list()
		return list? list[0]: null
	}
    
	def beforeSave = {
		value = value?.trim()?.toUpperCase()
	}
	
	def beforeUpdate = beforeSave
	def beforeInsert = beforeSave
}

