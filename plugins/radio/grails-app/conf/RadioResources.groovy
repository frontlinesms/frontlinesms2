modules = {
	common {
		dependsOn 'jquery, jquery-ui' // need to redefine these to make sure they are loaded first
		resource url:[dir:'css', file:'radio.css']
		resource url:[dir:'js', file:'on_air.js'], disposition:'head'
	}
}

