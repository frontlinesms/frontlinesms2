modules = {
	common { dependsOn 'frontlinesms-radio' }
	'frontlinesms-radio' {
		dependsOn 'frontlinesms-core'
		resource url:[dir:'css', file:'radio.css']
		resource url:[dir:'css', file:'jqcloud.css']
		resource url:[dir:'js', file:'on_air.js'], disposition:'head'
		resource url:[dir:'js', file:'jqcloud-1.0.0.js'], disposition:'head'
		resource url:[dir:'js', file:'word_cloud.js'], disposition:'head'
	}
}

