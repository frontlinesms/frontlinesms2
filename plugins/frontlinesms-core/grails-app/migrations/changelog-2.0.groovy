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

	// changeSet(author: "geoffrey", id:"1355230052153-35") {
	// 	grailsChange{
	// 		change{
				
	// 		}
	// 	}
	// }

	//make this changelog work with preexisting polls
	changeSet(author: "geoffrey (generated)", id: "1355230052153-36") {
		dropColumn(columnName: "ALIASES", tableName: "POLL_RESPONSE")
	}

	changeSet(author: "geoffrey (generated)", id: "1355230052153-37") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "IS_TOP_LEVEL", tableName: "KEYWORD")
	}
}
