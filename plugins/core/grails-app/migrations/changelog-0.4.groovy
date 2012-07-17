databaseChangeLog = {
	changeSet(author: "vaneyck (generated)", id: "1341328177902-1") {
		addColumn(tableName: "poll_response") {
			column(name: "aliases", type: "varchar(255)")
		}
	}

	changeSet(author: "sitati", id:"1341328177902-2") {
		grailsChange{
			change{
				sql.executeUpdate("UPDATE poll_response SET aliases = key where key <> 'unknown'")
			}
		}
	}
}
