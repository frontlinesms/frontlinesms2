databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "1341821687497-1") {
		addColumn(tableName: "poll_response") {
			column(name: "aliases", type: "varchar(255)")
		}
	}
}
