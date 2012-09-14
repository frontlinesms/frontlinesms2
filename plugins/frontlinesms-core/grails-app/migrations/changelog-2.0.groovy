databaseChangeLog = {

	changeSet(author: "sitati (generated)", id: "1347629647261-1") {
		createTable(tableName: "request_parameter") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "request_paramPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "connection_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1347629647261-2") {
		createTable(tableName: "web_connection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "web_connectioPK")
			}

			column(name: "http_method", type: "varchar(255)")

			column(name: "url", type: "varchar(255)")
		}
	}

	changeSet(author: "sitati (generated)", id: "1347629647261-4") {
		addForeignKeyConstraint(baseColumnNames: "connection_id", baseTableName: "request_parameter", constraintName: "FKF047F9F9D6552076", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "web_connection", referencesUniqueColumn: "false")
	}

}
