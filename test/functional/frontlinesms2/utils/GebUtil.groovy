package frontlinesms2.utils
import geb.*
import geb.navigator.*

@Category(grails.plugin.geb.GebSpec)
class GebUtil {
	
	def getTableAsArray(Navigator navigator, boolean stripHeader) {
		def rows = []
	    navigator*.each {
	    	def child = it.children()
	    	def row = []
		    child.each {
			    row << it.text()
			}
	    	rows << row
	    }
	    if(rows.size() > 0 && stripHeader)
			rows.remove(0) //Remove header
		rows
	}
	
	def getColumnAsArray(Navigator navigator, int i) {
		getTableAsArray(navigator, true).collect { it -> it[i]}
	}
}
