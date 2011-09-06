package frontlinesms2.search

class SearchPage extends geb.Page {
	static url = 'search/result?searchString=hi'
	static at = {
		title.startsWith("Search")
	}
}

