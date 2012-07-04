databaseChangeLog = {

	changeSet(author: "vaneyck (generated)", id: "1341328177902-1") {
		addColumn(tableName: "poll_response") {
			column(name: "aliases", type: "varchar(255)")
		}
	}
}
