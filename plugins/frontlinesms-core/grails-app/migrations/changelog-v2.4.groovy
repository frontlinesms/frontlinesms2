databaseChangeLog = {

	//Rename table SMSSYNC_DISPATCH to QUEUED_DISPATCH 
	changeSet(author: "vaneyck (by hand)", id: "rename-smssync-dispatch-table-1372420837873-1") {
		renameTable(oldTableName: "SMSSYNC_DISPATCH", newTableName: "QUEUED_DISPATCH")
	}

	changeSet(author: "vaneyck (generated)", id: "1387530956381-1") {
		addColumn(tableName: "FCONNECTION_FMESSAGE") {
			column(name: "TEXT_MESSAGE_ID", type: "BIGINT")
		}
	}

	// Copy all fconnection_fmessage[i].fmessage_id to fconnection_fmessage[i].text_message_id
        changeSet(author: "vaneyck", id:"1387530956381-4") {
                grailsChange{
                        change{
				sql.executeUpdate("UPDATE FCONNECTION_FMESSAGE SET TEXT_MESSAGE_ID = FMESSAGE_ID")
                        }   
                }   
        }

	//Add CLASS property to fmessage table
	changeSet(author: "vaneyck (generated)", id: "1400489400949-2") {
		addColumn(tableName: "FMESSAGE") {
			column(name: "CLASS", type: "VARCHAR(255)") {
				constraints(nullable: "true")
			}
		}
	}

	//Set all fmessage.class to frontlinesms2.TextMessage
        changeSet(author: "vaneyck", id:"1387530956381-3") {
                grailsChange{
                        change{
				sql.executeUpdate("UPDATE FMESSAGE SET CLASS = 'frontlinesms2.TextMessage'")
                        }   
                }   
        }

 	changeSet(author: "vaneyck (by hand)", id: "addNotNullConstraint-to-fmessage.class-1387530956381-3") {
		addNotNullConstraint(columnDataType: "VARCHAR", columnName: "CLASS", tableName: "FMESSAGE")
	}

	//Move FCONNECTION_FMESSAGE data to the FMESSAGE table CONNECTION_ID
	changeSet(author: "vaneyck (generated)", id: "1400489400949-3") {
		addColumn(tableName: "FMESSAGE") {
			column(name: "CONNECTION_ID", type: "BIGINT")
		}
		grailsChange {
			change {
				sql.execute 'UPDATE FMESSAGE M SET CONNECTION_ID = (SELECT FCONNECTION_MESSAGES_ID FROM FCONNECTION_FMESSAGE FM WHERE FM.TEXT_MESSAGE_ID = M.ID)'
			}
		}

	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-4") {
		addColumn(tableName: "FRONTLINESYNC_FCONNECTION") {
			column(name: "CHECK_INTERVAL", type: "INT") {
				constraints(nullable: "true")
			}
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-5") {
		addColumn(tableName: "FRONTLINESYNC_FCONNECTION") {
			column(name: "CONFIG_SYNCED", type: "BOOLEAN", defaultValue: true) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-6") {
		addColumn(tableName: "FRONTLINESYNC_FCONNECTION") {
			column(name: "HAS_DISPATCHES", type: "BOOLEAN", defaultValue: true) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-7") {
		addColumn(tableName: "FRONTLINESYNC_FCONNECTION") {
			column(name: "MISSED_CALL_ENABLED", type: "BOOLEAN", defaultValue: true) {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-8") {
		addColumn(tableName: "SMSSYNC_FCONNECTION") {
			column(name: "HAS_DISPATCHES", type: "BOOLEAN", defaultValue: true) {
				constraints(nullable: "false")
			}
		}
	}

	//Set default values for FRONTLINESYNC_FCONNECTION properties [CONFIG_SYNCED, MISSED_CALL_ENABLED, CHECK_INTERVAL]
	changeSet(author: "vaneyck (by hand)", id: "set_default_values_for_smssync_and_frontlinesync_properties") {
		grailsChange{
			change{
				sql.executeUpdate("UPDATE FRONTLINESYNC_FCONNECTION SET CONFIG_SYNCED = 1")
				sql.executeUpdate("UPDATE FRONTLINESYNC_FCONNECTION SET MISSED_CALL_ENABLED = 1")
				sql.executeUpdate("UPDATE FRONTLINESYNC_FCONNECTION SET CHECK_INTERVAL = 1")
			}
		}
	}

	// FRONTLINESYNC_FCONNECTION.CHECK_INTERVAL should be nullable	
	changeSet(author: "vaneyck|sitati (by hand)", id: "added-check-interval-to-frontlinesync-table-1372420837874-2") {
		addNotNullConstraint(columnDataType: "BIGINT", columnName: "CHECK_INTERVAL", tableName: "FRONTLINESYNC_FCONNECTION")
	}

	//Set default values for HAS_DISPATCHES on FRONTLINESYNC_FCONNECTION and SMSSYNC_FCONNECTION
	changeSet(author: "vaneyck (generated)", id: "1399636740836-3") {
		grailsChange {
			change {
				sql.executeUpdate("UPDATE FRONTLINESYNC_FCONNECTION SET HAS_DISPATCHES = 1")
				sql.executeUpdate("UPDATE SMSSYNC_FCONNECTION SET HAS_DISPATCHES = 1")
			}
		}
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-9") {
		dropNotNullConstraint(columnDataType: "VARCHAR(1600)", columnName: "TEXT", tableName: "FMESSAGE")
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-10") {
		modifyDataType(columnName: "TEXT", newDataType: "VARCHAR(511)", tableName: "SYSTEM_NOTIFICATION")
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-12") {
		dropForeignKeyConstraint(baseTableName: "FCONNECTION_FMESSAGE", baseTableSchemaName: "PUBLIC", constraintName: "FKD49CD6FC51C23BBF")
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-13") {
		dropForeignKeyConstraint(baseTableName: "FCONNECTION_FMESSAGE", baseTableSchemaName: "PUBLIC", constraintName: "FKD49CD6FC92DDC012")
	}
	
	changeSet(author: "vaneyck (generated)", id: "1400489400949-17") {
		dropColumn(columnName: "VERSION", tableName: "FCONNECTION")
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-18") {
		dropTable(tableName: "FCONNECTION_FMESSAGE")
	}

	changeSet(author: "vaneyck (generated)", id: "1400489400949-19") {
		dropTable(tableName: "SMSSYNC_DISPATCH")
	}
}
