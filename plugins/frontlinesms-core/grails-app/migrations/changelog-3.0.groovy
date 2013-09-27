databaseChangeLog = {

	changeSet(author: "sitati (generated)", id: "1379593412207-1") {
		createTable(tableName: "custom_activity") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "custom_activiPK")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-2") {
		createTable(tableName: "message_detail") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "message_detaiPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "message_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "owner_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "owner_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	// Migrate old Fmessage.ownerDetail entries to new Message Detail domain
	// can safely assume that all existing owners are ACTIVITY, not STEP, as custom activity not released yet
	changeSet(author: "sitati", id:"1379593412207-3") {
		grailsChange{
			change{
				sql.eachRow("SELECT * FROM FMESSAGE") { fmessage ->
					if(fmessage.OWNER_DETAIL && fmessage.MESSAGE_OWNER_ID) {
						sql.execute("INSERT INTO message_detail (version, message_id, owner_id, owner_type, value) VALUES (0, ${fmessage.ID}, ${fmessage.MESSAGE_OWNER_ID}, 'ACTIVITY', '${fmessage.OWNER_DETAIL}')")
					}
				}
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-4") {
		createTable(tableName: "nexmo_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "nexmo_fconnecPK")
			}

			column(name: "api_key", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "api_secret", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "from_number", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-5") {
		createTable(tableName: "smpp_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "smpp_fconnectPK")
			}

			column(name: "from_number", type: "varchar(255)")

			column(name: "smpp_password", type: "varchar(255)")

			column(name: "port", type: "varchar(255)")

			column(name: "receive", type: "boolean")

			column(name: "send", type: "boolean")

			column(name: "url", type: "varchar(255)")

			column(name: "username", type: "varchar(255)")
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-6") {
		createTable(tableName: "step") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "stepPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "activity_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "steps_idx", type: "integer")
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-7") {
		createTable(tableName: "step_property") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "step_propertyPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "key", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "step_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-8") {
		addColumn(tableName: "dispatch") {
			column(name: "fconnection_id", type: "bigint")
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-9") {
		addColumn(tableName: "fconnection") {
			column(name: "enabled", type: "boolean")
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-10") {
		addColumn(tableName: "fconnection") {
			column(name: "receive_enabled", type: "boolean")
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-11") {
		addColumn(tableName: "fconnection") {
			column(name: "send_enabled", type: "boolean")
		}
	}

	// Set default values for fconnection.send_enabled, fconnection.recieve_enabled and fconnection.enabled
	changeSet(author: "sitati", id:"1379593412207-12") {
		grailsChange{
			change{
				// prior to this, existing fconnection implementations are 2-way, except clickatell
				sql.executeUpdate("UPDATE FCONNECTION SET send_enabled = true, receive_enabled = true, enabled = true")
				sql.executeUpdate("UPDATE FCONNECTION SET receive_enabled = false where ID in (select ID from CLICKATELL_FCONNECTION)")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-13") {
		addColumn(tableName: "search") {
			column(name: "starred_only", type: "boolean")
		}
		grailsChange{
			change{
				sql.executeUpdate("UPDATE SEARCH SET starred_only = false")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-14") {
		addColumn(tableName: "smssync_fconnection") {
			column(name: "timeout", type: "integer")
		}
		grailsChange{
			change{
				sql.executeUpdate("UPDATE smssync_fconnection SET timeout = 360")
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-15") {
		addColumn(tableName: "system_notification") {
			column(name: "topic", type: "varchar(255)")
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-16") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "API_ID", tableName: "CLICKATELL_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-17") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "CLICKATELL_PASSWORD", tableName: "CLICKATELL_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-18") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "SEND_TO_USA", tableName: "CLICKATELL_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-19") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "USERNAME", tableName: "CLICKATELL_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-20") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "KEY", tableName: "POLL_RESPONSE")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-21") {
		dropForeignKeyConstraint(baseTableName: "POLL_RESPONSE_FMESSAGE", baseTableSchemaName: "PUBLIC", constraintName: "FK76CBE69F92DDC012")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-22") {
		addForeignKeyConstraint(baseColumnNames: "message_id", baseTableName: "message_detail", constraintName: "FK21B74F893FBE872C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "fmessage", referencesUniqueColumn: "false")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-23") {
		addForeignKeyConstraint(baseColumnNames: "activity_id", baseTableName: "step", constraintName: "FK3606CC931DFDE3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "custom_activity", referencesUniqueColumn: "false")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-24") {
		addForeignKeyConstraint(baseColumnNames: "step_id", baseTableName: "step_property", constraintName: "FK9E9EDE8A35C3032", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "step", referencesUniqueColumn: "false")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-25") {
		dropColumn(columnName: "OWNER_DETAIL", tableName: "FMESSAGE")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-26") {
		dropColumn(columnName: "RECEIVE", tableName: "INTELLI_SMS_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-28") {
		dropColumn(columnName: "SEND", tableName: "INTELLI_SMS_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-29") {
		dropColumn(columnName: "RESPONSES_IDX", tableName: "POLL_RESPONSE")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-30") {
		dropColumn(columnName: "RECEIVE", tableName: "SMSLIB_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-31") {
		dropColumn(columnName: "SEND", tableName: "SMSLIB_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-32") {
		dropColumn(columnName: "RECEIVE_ENABLED", tableName: "SMSSYNC_FCONNECTION")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-33") {
		dropColumn(columnName: "SEND_ENABLED", tableName: "SMSSYNC_FCONNECTION")
	}

	// Migrate poll_response_fmessage data into message_detail 
	changeSet(author: "sitati", id:"1379593412207-34") {
		grailsChange{
			change{
				sql.eachRow("SELECT * FROM POLL_RESPONSE_FMESSAGE") { prf ->
					def messageDetailValue
					def pollId
					sql.eachRow("SELECT * FROM POLL_RESPONSE where ID = ${prf.POLL_RESPONSE_ID}"){ pollResponse ->
						messageDetailValue = (pollResponse.key == 'unknown') ? 'unknown' : pollResponse.id
						pollId = pollResponse.POLL_ID
					}
					sql.execute("INSERT INTO message_detail (version, message_id, owner_id, owner_type, value) VALUES (0, ${pfr.FMESSAGE_ID}, ${pollId}, 'ACTIVITY', '${messageDetailValue}')")
				}
			}
		}
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-35") {
		dropTable(tableName: "POLL_RESPONSE_FMESSAGE")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-36") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "enabled", tableName: "fconnection")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-37") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "send_enabled", tableName: "fconnection")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-38") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "recieve_enabled", tableName: "fconnection")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-39") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "starred_only", tableName: "search")
	}

	changeSet(author: "sitati (generated)", id: "1379593412207-40") {
		addNotNullConstraint(columnDataType: "integer", columnName: "timeout", tableName: "smssync_fconnection")
	}
}
