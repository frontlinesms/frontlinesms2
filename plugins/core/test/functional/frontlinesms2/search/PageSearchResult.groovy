package frontlinesms2.search
import frontlinesms2.message.*

class PageSearchResult extends PageSearch {
	static url = 'search/result/show'
	static at = {
		title.startsWith("Results")
	}

	String convertToPath(Object[] args) {
		println("Building Search url...")
		def restOfPath = ""
		if (!args)
			return ""

	    if (args[0] instanceof String)
			restOfPath += "?searchString="+args[0]
		if (args[1] instanceof String){
			if (args[1]){
				restOfPath += "&"+args[1]
			}
		}
	    return restOfPath
	}
}
