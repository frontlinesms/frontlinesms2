databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "1337327496462-1") {
		createTable(tableName: "activity") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "activityPK")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "sent_message_text", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-2") {
		createTable(tableName: "announcement") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "announcementPK")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-3") {
		createTable(tableName: "autoreply") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "autoreplyPK")
			}

			column(name: "autoreply_text", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-4") {
		createTable(tableName: "clickatell_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "clickatell_fcPK")
			}

			column(name: "api_id", type: "varchar(255)")

			column(name: "clickatell_password", type: "varchar(255)")

			column(name: "username", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-5") {
		createTable(tableName: "contact") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "contactPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)")

			column(name: "mobile", type: "varchar(255)") {
				constraints(unique: "true")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "notes", type: "varchar(1024)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-6") {
		createTable(tableName: "custom_field") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "custom_fieldPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "contact_id", type: "bigint")

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "smart_group_id", type: "bigint")

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-7") {
		createTable(tableName: "dispatch") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "dispatchPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "date_sent", type: "timestamp")

			column(name: "dst", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "is_deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "message_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-8") {
		createTable(tableName: "email_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "email_fconnecPK")
			}

			column(name: "email_password", type: "varchar(255)")

			column(name: "receive_protocol", type: "varchar(255)")

			column(name: "server_name", type: "varchar(255)")

			column(name: "server_port", type: "integer")

			column(name: "username", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-9") {
		createTable(tableName: "fconnection") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fconnectionPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-10") {
		createTable(tableName: "fmessage") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fmessagePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "archived", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "inbound", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "is_deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "message_owner_id", type: "bigint")

			column(name: "read", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "src", type: "varchar(255)")

			column(name: "starred", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "text", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-11") {
		createTable(tableName: "folder") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "folderPK")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-12") {
		createTable(tableName: "group_member") {
			column(name: "group_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "contact_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-13") {
		createTable(tableName: "grup") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "grupPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false", unique: "true")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-14") {
		createTable(tableName: "intelli_sms_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "intelli_sms_fPK")
			}

			column(name: "email_password", type: "varchar(255)")

			column(name: "email_user_name", type: "varchar(255)")

			column(name: "send_password", type: "varchar(255)")

			column(name: "receive", type: "boolean")

			column(name: "receive_protocol", type: "varchar(255)")

			column(name: "send", type: "boolean")

			column(name: "server_name", type: "varchar(255)")

			column(name: "server_port", type: "integer")

			column(name: "username", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-15") {
		createTable(tableName: "keyword") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "keywordPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "activity_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-16") {
		createTable(tableName: "log_entry") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "log_entryPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "content", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "date", type: "timestamp") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-17") {
		createTable(tableName: "message_owner") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "message_ownerPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "archived", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-18") {
		createTable(tableName: "poll") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pollPK")
			}

			column(name: "autoreply_text", type: "varchar(255)")

			column(name: "question", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-19") {
		createTable(tableName: "poll_response") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "poll_responsePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "key", type: "varchar(255)")

			column(name: "poll_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "responses_idx", type: "integer")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-20") {
		createTable(tableName: "poll_response_fmessage") {
			column(name: "poll_response_messages_id", type: "bigint")

			column(name: "fmessage_id", type: "bigint")

			column(name: "messages_idx", type: "integer")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-21") {
		createTable(tableName: "search") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "searchPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "activity_id", type: "varchar(255)")

			column(name: "contact_string", type: "varchar(255)")

			column(name: "end_date", type: "timestamp")

			column(name: "group_id", type: "bigint")

			column(name: "in_archive", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "search_string", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "start_date", type: "timestamp")

			column(name: "status", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-22") {
		createTable(tableName: "search_custom_fields") {
			column(name: "custom_fields", type: "bigint")

			column(name: "custom_fields_idx", type: "varchar(255)")

			column(name: "custom_fields_elt", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-23") {
		createTable(tableName: "smart_group") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "smart_groupPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "contact_name", type: "varchar(255)")

			column(name: "email", type: "varchar(255)")

			column(name: "mobile", type: "varchar(255)")

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "notes", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-24") {
		createTable(tableName: "smslib_fconnection") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "smslib_fconnePK")
			}

			column(name: "all_messages", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "baud", type: "integer") {
				constraints(nullable: "false")
			}

			column(name: "imsi", type: "varchar(255)")

			column(name: "pin", type: "varchar(255)")

			column(name: "port", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "serial", type: "varchar(255)")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-25") {
		createTable(tableName: "system_notification") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "system_notifiPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "read", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "text", type: "varchar(255)") {
				constraints(nullable: "false", unique: "true")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-26") {
		createTable(tableName: "trash") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trashPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "display_name", type: "varchar(255)")

			column(name: "display_text", type: "varchar(255)")

			column(name: "object_class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "object_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-27") {
		addPrimaryKey(columnNames: "group_id, contact_id", constraintName: "group_memberPK", tableName: "group_member")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-28") {
		createIndex(indexName: "mobile_unique_1337327496323", tableName: "contact", unique: "true") {
			column(name: "mobile")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-29") {
		createIndex(indexName: "name_unique_1337327496388", tableName: "grup", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-30") {
		createIndex(indexName: "text_unique_1337327496412", tableName: "system_notification", unique: "true") {
			column(name: "text")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-31") {
		addForeignKeyConstraint(baseColumnNames: "contact_id", baseTableName: "custom_field", constraintName: "FK2ACD76AC6256D8C2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "contact", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-32") {
		addForeignKeyConstraint(baseColumnNames: "smart_group_id", baseTableName: "custom_field", constraintName: "FK2ACD76AC3BEF92BF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "smart_group", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-33") {
		addForeignKeyConstraint(baseColumnNames: "message_id", baseTableName: "dispatch", constraintName: "FK10F9447A3FBE872C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "fmessage", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-34") {
		addForeignKeyConstraint(baseColumnNames: "message_owner_id", baseTableName: "fmessage", constraintName: "FK9CA2E0E13742043", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "message_owner", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-35") {
		addForeignKeyConstraint(baseColumnNames: "contact_id", baseTableName: "group_member", constraintName: "FKE926145A6256D8C2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "contact", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-36") {
		addForeignKeyConstraint(baseColumnNames: "group_id", baseTableName: "group_member", constraintName: "FKE926145A9083EA62", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "grup", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-37") {
		addForeignKeyConstraint(baseColumnNames: "activity_id", baseTableName: "keyword", constraintName: "FKCF751DE96E816952", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "activity", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-38") {
		addForeignKeyConstraint(baseColumnNames: "poll_id", baseTableName: "poll_response", constraintName: "FK7FECF4C1FC80A752", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "poll", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-39") {
		addForeignKeyConstraint(baseColumnNames: "fmessage_id", baseTableName: "poll_response_fmessage", constraintName: "FK76CBE69F92DDC012", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "fmessage", referencesUniqueColumn: "false")
	}

	changeSet(author: "geoffrey (generated)", id: "1337327496462-40") {
		addForeignKeyConstraint(baseColumnNames: "group_id", baseTableName: "search", constraintName: "FKC9FA65A89083EA62", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "grup", referencesUniqueColumn: "false")
	}
}
