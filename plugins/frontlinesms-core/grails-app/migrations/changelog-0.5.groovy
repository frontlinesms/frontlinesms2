databaseChangeLog = {

	changeSet(author: "vaneyck (generated)", id: "1346749118687-1") {
		createTable(tableName: "subscription") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "subscriptionPK")
			}

			column(name: "default_action", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "join_aliases", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "join_autoreply_text", type: "varchar(255)")

			column(name: "leave_aliases", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "leave_autoreply_text", type: "varchar(255)")
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1346749118687-2") {
		addColumn(tableName: "fmessage") {
			column(name: "owner_detail", type: "varchar(255)")
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1346749118687-3") {
		addNotNullConstraint(columnDataType: "varchar(1600)", columnName: "TEXT", tableName: "FMESSAGE")
	}

	changeSet(author: "vaneyck (generated)", id: "1346749118687-4") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "RECEIVE", tableName: "SMSLIB_FCONNECTION")
	}

	changeSet(author: "vaneyck (generated)", id: "1346749118687-5") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "SEND", tableName: "SMSLIB_FCONNECTION")
	}

	changeSet(author: "vaneyck (generated)", id: "1346749118687-7") {
		addForeignKeyConstraint(baseColumnNames: "group_id", baseTableName: "subscription", constraintName: "FK1456591D9083EA62", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "grup", referencesUniqueColumn: "false")
	}
}
