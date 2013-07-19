package frontlinesms2.search
import frontlinesms2.message.*

class PageNewSearch extends PageSearch {
	static url = 'search/no_search'
	
	static at = {
		title.startsWith("Search")
	}
}

