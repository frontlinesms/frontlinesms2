modules = {
	radio {
		dependsOn "messages"
		resource url: [dir:'css', file:'radio.css']
		resource url: [dir:'js', file:"on_air.js"], disposition: 'head'
	}
}
