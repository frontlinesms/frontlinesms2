eventDefaultStart = {
	createUnitTest = { Map args = [:] ->		
		createSpec('unit', args)
	}
	createIntegrationTest = { Map args = [:] ->
		createSpec('integration', args)
	}
	createSpec = { String type, Map args	 ->
		def superClass
		// map test superclass to Spock equivalent
		switch(args["superClass"]) {
			case "Controller${type.capitalize()}TestCase":
				superClass = "ControllerSpec"
				break
			case "TagLibUnitTestCase":
				superClass = "TagLibSpec"
				break
			default:
				superClass = "UnitSpec"
		}
		createArtifact name: args["name"], suffix: "${args['suffix']}Spec", type: "Spec", path: "test/${type}", superClass: superClass
	}
}
