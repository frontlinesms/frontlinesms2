databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "1357564215965-1") {
		createTable(tableName: "radio_show") {
			column(name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "radio_showPK")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "is_running", type: "boolean") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1357564215965-2") {
		createTable(tableName: "radio_show_activity") {
			column(name: "radio_show_activities_id", type: "bigint")

			column(name: "activity_id", type: "bigint")

			column(name: "activities_idx", type: "integer")
		}
	}

	changeSet(author: "geoffrey (generated)", id: "1357564215965-4") {
		addForeignKeyConstraint(baseColumnNames: "activity_id", baseTableName: "radio_show_activity", constraintName: "FKE4CB28CD6E816952", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "activity", referencesUniqueColumn: "false")
	}
}
