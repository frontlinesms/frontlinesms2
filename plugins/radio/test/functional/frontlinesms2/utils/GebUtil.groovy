package frontlinesms2.utils
import geb.*
import geb.navigator.*

@Category(geb.spock.GebReportingSpec)
class GebUtil {
	def getColumnText(tableId, columnIndex) {
		return $("table#$tableId tr td:nth-child(${columnIndex+1})")*.text()
	}
}
