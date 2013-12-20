databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "1387266424769-1") {
		createTable(tableName: "frontlinesync_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "frontlinesyncPK")
			}

			column(name: "last_connection_time", type: "timestamp")

			column(name: "secret", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1387266424769-2") {
		renameColumn(tableName: "fmessage", oldColumnName: "READ", newColumnName: "rd")
	}
	
	changeSet(author: "geoffrey (generated)", id: "1387266424769-4") {
		renameColumn(tableName: "system_notification", oldColumnName: "READ", newColumnName: "rd")
	}

	changeSet(author: "geoffrey (generated)", id: "1387266424769-3") {
		addColumn(tableName: "smssync_fconnection") {
			column(name: "last_connection_time", type: "timestamp")
		}
	}
}
