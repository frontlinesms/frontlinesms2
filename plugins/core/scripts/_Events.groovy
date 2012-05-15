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


boolean inFunctionalTestPhase
eventTestPhaseStart = { phaseName ->
	inFunctionalTestPhase = (phaseName == 'functional')
	junitReportStyleDir = "test/conf"
}

eventTestStart = { testName ->
	if (inFunctionalTestPhase) {
		def sql = Sql.newInstance('jdbc:h2:mem:testDb', 'sa', '', 'org.h2.Driver')
		sql.execute "SET REFERENTIAL_INTEGRITY FALSE"
		sql.eachRow("SHOW TABLES") { table -> sql.execute('DELETE FROM ' + table.TABLE_NAME) } 
		sql.execute "SET REFERENTIAL_INTEGRITY TRUE"
	}

}

eventTestPhaseEnd = { phaseName ->
	if (phaseName == 'functional') {
		def report = new File('target/test-reports/html/screenshots.html')
		new File('target/test-reports/geb').eachFileRecurse { f ->
			if(!f.name.endsWith('.png')) return
			report.append '<img height="120" src="..' +
			f.path.substring('target/test-reports'.size()) + '"/>\n'
		}
	}
}

