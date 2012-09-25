class ApiUrlMappings {
	static mappings = {
		"/" controller:'dashboard' // TODO remap this using a controller so the URL is written properly
		"/api/1/$entityClassApiUrl/$entityId/$secret?" controller:'api'
		"/dashboard/$action?" controller:'dashboard'
		"/login/$action?" controller:'login'
		"/logout/$action?" controller:'logout'
	}
}

