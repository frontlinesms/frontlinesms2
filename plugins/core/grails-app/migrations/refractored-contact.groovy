databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "1335950784189-1") {
		renameColumn(tableName: "contact", oldColumnName:"primary_mobile", newColumnName:"mobile")
	}

	changeSet(author: "geoffrey (generated)", id: "1335950784189-2") {
		createIndex(indexName: "mobile_unique_1335950783945", tableName: "contact", unique: "true") {
			column(name: "mobile")
		}
	}

//	changeSet(author: "geoffrey (generated)", id: "1335950784189-3") {
//		dropIndex(indexName: "CONSTRAINT_INDEX_6", tableName: "CONTACT")
//	}

	changeSet(author: "geoffrey (generated)", id: "1335950784189-5") {
		dropColumn(columnName: "SECONDARY_MOBILE", tableName: "CONTACT")
	}
}
