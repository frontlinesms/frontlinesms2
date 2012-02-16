package frontlinesms2.utils
import geb.*
import geb.navigator.*

@Category(grails.plugin.geb.GebSpec)
class GebUtil {
	def getColumnText(tableId, columnIndex) {
		return $("#$tableId tr td:nth-child(${columnIndex+1})")*.text()
	}
}
