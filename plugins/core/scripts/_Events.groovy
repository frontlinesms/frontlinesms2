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
	junitReportStyleDir = "test/conf"
}

eventTestStart = { name ->
	if (inFunctionalTestPhase) {
		def sql = Sql.newInstance('jdbc:h2:mem:testDb', 'sa', '', 'org.h2.Driver')
		sql.execute "SET REFERENTIAL_INTEGRITY FALSE"
		sql.eachRow("SHOW TABLES") { table -> sql.execute('DELETE FROM ' + table.TABLE_NAME) } 
		sql.execute "SET REFERENTIAL_INTEGRITY TRUE"
	}

}
