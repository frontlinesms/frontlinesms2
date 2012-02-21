package frontlinesms2.search

class PageSearchResultI extends PageSearchResult {
	static getUrl() { "search/result/show?searchString=i" }
	
	static content = {
		archiveAllBtn { $('#btn_archive_all') }
	}
}
