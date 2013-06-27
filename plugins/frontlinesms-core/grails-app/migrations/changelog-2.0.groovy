databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "1355230052153-1") {
		createTable(tableName: "autoforward") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "autoforwardPK")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-2") {
		createTable(tableName: "autoforward_contact") {
			column(name: "autoforward_contacts_id", type: "bigint")

			column(name: "contact_id", type: "bigint")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-3") {
		createTable(tableName: "autoforward_grup") {
			column(name: "autoforward_groups_id", type: "bigint")

			column(name: "group_id", type: "bigint")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-4") {
		createTable(tableName: "autoforward_smart_group") {
			column(name: "autoforward_smart_groups_id", type: "bigint")

			column(name: "smart_group_id", type: "bigint")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-5") {
		createTable(tableName: "fconnection_fmessage") {
			column(name: "fconnection_messages_id", type: "bigint")

			column(name: "fmessage_id", type: "bigint")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-6") {
		createTable(tableName: "generic_webconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "generic_webcoPK")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-7") {
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

	changeSet(author: "geoffrey (generated)", id: "1355230052153-8") {
		createTable(tableName: "smssync_dispatch") {
			column(name: "connection_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "dispatch_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-9") {
		createTable(tableName: "smssync_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "smssync_fconnPK")
			}

			column(name: "receive_enabled", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "secret", type: "varchar(255)")

			column(name: "send_enabled", type: "boolean") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-10") {
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

			column(name: "join_autoreply_text", type: "varchar(255)")

			column(name: "leave_autoreply_text", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-11") {
		createTable(tableName: "ushahidi_webconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "ushahidi_webcPK")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-12") {
		createTable(tableName: "webconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "webconnectionPK")
			}

			column(name: "api_enabled", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "http_method", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "secret", type: "varchar(255)")

			column(name: "url", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-13") {
		addColumn(tableName: "clickatell_fconnection") {
			column(name: "from_number", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-14") {
		addColumn(tableName: "clickatell_fconnection") {
			column(name: "send_to_usa", type: "boolean")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-15") {
		addColumn(tableName: "fmessage") {
			column(name: "owner_detail", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-16") {
		//Not null contraint is added at the end of this script
		addColumn(tableName: "keyword") {
			column(name: "is_top_level", type: "boolean")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-17") {
		addColumn(tableName: "keyword") {
			column(name: "keywords_idx", type: "integer")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-18") {
		addColumn(tableName: "keyword") {
			column(name: "owner_detail", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-20") {
		addColumn(tableName: "smslib_fconnection") {
			column(name: "manufacturer", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-21") {
		addColumn(tableName: "smslib_fconnection") {
			column(name: "model", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-22") {
		addNotNullConstraint(columnDataType: "varchar(1600)", columnName: "TEXT", tableName: "FMESSAGE")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-23") {
		addPrimaryKey(columnNames: "connection_id, dispatch_id", constraintName: "smssync_dispaPK", tableName: "smssync_dispatch")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-25") {
		addForeignKeyConstraint(baseColumnNames: "autoforward_contacts_id", baseTableName: "autoforward_contact", constraintName: "FK8954F717D290A53C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "autoforward", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-26") {
		addForeignKeyConstraint(baseColumnNames: "contact_id", baseTableName: "autoforward_contact", constraintName: "FK8954F7176256D8C2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "contact", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-27") {
		addForeignKeyConstraint(baseColumnNames: "autoforward_groups_id", baseTableName: "autoforward_grup", constraintName: "FK3C39752FC133A1DB", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "autoforward", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-28") {
		addForeignKeyConstraint(baseColumnNames: "group_id", baseTableName: "autoforward_grup", constraintName: "FK3C39752F9083EA62", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "grup", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-29") {
		addForeignKeyConstraint(baseColumnNames: "autoforward_smart_groups_id", baseTableName: "autoforward_smart_group", constraintName: "FK4D8334002A1445A5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "autoforward", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-30") {
		addForeignKeyConstraint(baseColumnNames: "smart_group_id", baseTableName: "autoforward_smart_group", constraintName: "FK4D8334003BEF92BF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "smart_group", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-31") {
		addForeignKeyConstraint(baseColumnNames: "fconnection_messages_id", baseTableName: "fconnection_fmessage", constraintName: "FKD49CD6FC51C23BBF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "fconnection", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-32") {
		addForeignKeyConstraint(baseColumnNames: "fmessage_id", baseTableName: "fconnection_fmessage", constraintName: "FKD49CD6FC92DDC012", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "fmessage", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-33") {
		addForeignKeyConstraint(baseColumnNames: "connection_id", baseTableName: "request_parameter", constraintName: "FKF047F9F95F834456", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "webconnection", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-34") {
		addForeignKeyConstraint(baseColumnNames: "group_id", baseTableName: "subscription", constraintName: "FK1456591D9083EA62", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "grup", referencesUniqueColumn: "false")
	}

	//> POLL, ALIAS AND KEYWORD TRANSFORMATIONS
	changeSet(author: "sitati", id:"1355230052153-35") {
		grailsChange{
			change{
				// first set all existing keywords as top-level, and as first in the keyword list for the poll
				sql.executeUpdate("UPDATE keyword SET is_top_level = true, keywords_idx = 0")
				sql.eachRow("SELECT * FROM POLL") { poll ->
					println "MIGRATIONS:::::::::: about to migrate poll: ${poll}"
					// check if poll has keywords (if not, it has automatic sorting disabled, no need to act on aliases)
					def pollKeywordCount = 0
					sql.eachRow("SELECT * FROM keyword WHERE activity_id = ${poll.ID}") { pollKeyword -> pollKeywordCount += 1 }
					if(pollKeywordCount) {
						def pollKeywordIndex = 1 // because top level keyword already set as zero
						sql.eachRow("SELECT * FROM POLL_RESPONSE WHERE POLL_ID = ${poll.ID}") { pollResponse -> 
							println "MIGRATIONS:::::::::: for poll: ${poll}, migrating poll response:::: ${pollResponse}"
							pollResponse.ALIASES?.split(',').each { aliasValue ->
								println "MIGRATIONS:::::::::: for poll: ${poll}, migrating poll response ${pollResponse}: alias::: ${aliasValue}"
								sql.execute("INSERT INTO keyword (activity_id, owner_detail, value, is_top_level, keywords_idx) values ($poll.ID, $pollResponse.KEY, ${aliasValue.trim().toUpperCase()}, false, $pollKeywordIndex)")
								pollKeywordIndex += 1
							}
						}
					}
					else {
						println "Poll had no keywords, skipping alias migration"
					}
					// update ${contact_name} and ${contact_number} substitutions to ${recipient_name} and ${recipient_number}
					if(poll.AUTOREPLY_TEXT?.contains('${contact_name}') || poll.AUTOREPLY_TEXT?.contains('${contact_number}')) {
						def newAutoreplyText = poll.AUTOREPLY_TEXT.replace('${contact_name}', '${recipient_name}').replace('${contact_number}', '${recipient_number}').replace('"', '\\"')
						sql.executeUpdate("UPDATE poll SET AUTOREPLY_TEXT = $newAutoreplyText WHERE poll.ID = ${poll.id}")
					}
				}
			}
		}
	}

	//> AUTOREPLY TRANSFORMATIONS
	changeSet(author: "sitati", id:"1355230052153-36") {
		grailsChange{
			change{
				sql.eachRow("SELECT * FROM AUTOREPLY") { autoreply ->
					// update ${contact_name} and ${contact_number} substitutions to ${recipient_name} and ${recipient_number}
					if(autoreply.AUTOREPLY_TEXT?.contains('${contact_name}') || autoreply.AUTOREPLY_TEXT?.contains('${contact_number}')) {
						def newAutoreplyText = autoreply.AUTOREPLY_TEXT.replace('${contact_name}', '${recipient_name}').replace('${contact_number}', '${recipient_number}').replace('"', '\\"')
						sql.executeUpdate("UPDATE autoreply SET AUTOREPLY_TEXT = '"+newAutoreplyText + "' WHERE autoreply.ID = ${autoreply.id}")
					}
				}
			}
		}
	}

	//> POLL RESPONSE ALIAS CLEANUP
	changeSet(author: "geoffrey (generated)", id: "1355230052153-37") {
		dropColumn(columnName: "ALIASES", tableName: "POLL_RESPONSE")
	}

	//> INSTATING REQUIRED NOT_NULL CONSTRAINT ON KEYWORD.IS_TOP_LEVEL
	changeSet(author: "geoffrey (generated)", id: "1355230052153-38") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "IS_TOP_LEVEL", tableName: "KEYWORD")
	}

	//> CLICKATEL FCONNECTION TRANSFORMATIONS
	changeSet(author: "sitati", id:"1355230052153-39") {
		grailsChange{
			change{
				sql.executeUpdate("UPDATE clickatell_fconnection SET send_to_usa = false")
			}
		}
	}
	
	changeSet(author: "geoffrey (generated)", id: "11355230052153-40") {
		createTable(tableName: "shujaa_sms_fconnection") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "shujaa_sms_fconnectionPK")
			}

			column(name: "username", type: "varchar(255)")

			column(name: "shujaa_password", type: "varchar(255)")
			
			column(name: "account", type: "varchar(255)")
			
			column(name: "source", type: "varchar(255)")
			
			column(name: "network", type: "varchar(255)")			
			
			
		}
	}
	
}
