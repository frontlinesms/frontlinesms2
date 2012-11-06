package frontlinesms2

class ControllerUtils {
	def withDomainObject(domainClass, Closure objectIdFetcher={ params.id }, Closure onFail=null) {
		return { c ->
			if(domainClass instanceof Closure) domainClass = domainClass.call()
			def objectId = objectIdFetcher.call()
			def o = objectId? domainClass.get(objectId): domainClass.newInstance()
			if(o) c.call(o)
			else {
				def failureText = message('default.not.found.message', args:[message(code:domainClass.shortName+'.label'), objectId])
				if(onFail) {
					flash.message = failureText
					onFail.call()
				} else render(text:failureText)
			}
		}
	}
}

