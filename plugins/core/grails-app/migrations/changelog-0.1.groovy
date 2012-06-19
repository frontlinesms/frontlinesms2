databaseChangeLog = {

	changeSet(author: "vaneyck (generated)", id: "1340023862332-1") {
		dropForeignKeyConstraint(baseTableName: "SEARCH", baseTableSchemaName: "PUBLIC", constraintName: "FKC9FA65A89083EA62")
	}

	changeSet(author: "vaneyck (generated)", id: "1340023862332-2") {
		dropTable(tableName: "SEARCH")
	}

	changeSet(author: "vaneyck (generated)", id: "1340023862332-3") {
		dropTable(tableName: "SEARCH_CUSTOM_FIELDS")
	}
}
