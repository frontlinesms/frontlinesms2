databaseChangeLog = {
	changeSet(author: "sitati (generated)", id: "1338300546197-1") {
		renameColumn(tableName: "poll", oldColumnName:"STANDARD", newColumnName:"yes_no")
	}

	changeSet(author: "user (generated)", id: "1339364051025-1") {
		['dispatch', 'fmessage', 'keyword', 'message_owner',
				'poll_response'].each { table ->
			dropColumn(tableName:table, columnName:'version')
		}

		modifyDataType(tableName:'fmessage', columnName:'text', newDataType:'varchar(1600)')
	}
}

