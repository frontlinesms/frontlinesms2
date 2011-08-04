package frontlinesms2.utils
import geb.*
import geb.navigator.*

@Category(grails.plugin.geb.GebSpec)
class GebUtil {
	
	def getTableAsArray(Navigator navigator) {
		def rows = []
	    navigator*.each {
	    	def child = it.children()
	    	def row = []
		    child.each {
			    row << it.text()
			}
	    	rows << row
	    }
	    rows
	}
	
	def getColumnAsArray(Navigator navigator, int i) {
		getTableAsArray(navigator).collect { it -> it[i]}
	}
}