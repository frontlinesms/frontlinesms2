import groovy.sql.Sql

eventDefaultStart = {
	createUnitTest = { Map args = [:] ->
		createSpec('unit', args)
	}
	createIntegrationTest = { Map args = [:] ->
		createSpec('integration', args)
	}
	createSpec = { String type, Map args ->
		def superClass
		// map test superclass to Spock equivalent
		switch (args["superClass"]) {
			case "Controller${type.capitalize()}TestCase":
				superClass = "ControllerSpec"
				break
			case "TagLibUnitTestCase":
				superClass = "TagLibSpec"
				break
		// TODO add a case for Camel Route integration test case
			default:
				superClass = "${type.capitalize()}Spec"
		}
		createArtifact name: args["name"], suffix: "${args['suffix']}Spec", type: "Spec", path: "test/${type}", superClass: superClass
	}

}


boolean inFunctionalTestPhase = false

eventTestPhaseStart = { name ->
	inFunctionalTestPhase = (name == 'functional')
}

eventTestStart = { name ->
	if (inFunctionalTestPhase) {
		println "In _Events.groovy"
		def sql = Sql.newInstance("jdbc:hsqldb:mem:testDb", "sa",
				"", "org.hsqldb.jdbcDriver")
		sql.execute "SET REFERENTIAL_INTEGRITY FALSE"
		sql.eachRow("SELECT * FROM  INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_SCHEM = 'PUBLIC'") { row ->
			println "deleting from ${row.TABLE_NAME}"
			sql.execute "DELETE FROM " + row.TABLE_NAME
		}

		sql.execute "SET REFERENTIAL_INTEGRITY TRUE"
	}

}
