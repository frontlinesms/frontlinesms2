package frontlinesms2.search

class PageSearchResultI extends PageSearchResult {
	static getUrl() { "search/result?searchString=i" }
	
	static content = {
		archiveAllBtn { $('#btn_archive_all') }
	}
}