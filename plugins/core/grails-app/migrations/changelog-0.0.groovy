databaseChangeLog = {

	changeSet(author: "sitati (generated)", id: "1338300546197-1") {
		renameColumn(tableName: "poll", oldColumnName:"STANDARD", newColumnName:"yes_no")
	}
}
