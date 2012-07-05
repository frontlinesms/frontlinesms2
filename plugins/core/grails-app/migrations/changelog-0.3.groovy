databaseChangeLog = {
	changeSet(author: "vaneyck (generated)", id: "1341328177902-1") {
		addColumn(tableName: "poll_response") {
			column(name: "aliases", type: "varchar(255)")

	changeSet(author: "geoffrey (generated)", id: "1341237212656-1") {
		addColumn(tableName: "smslib_fconnection") {
			column(name: "receive", type: "boolean") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1341237212656-2") {
		addColumn(tableName: "smslib_fconnection") {
			column(name: "send", type: "boolean") {
				constraints(nullable: "false")
			}
		}
	}
}
