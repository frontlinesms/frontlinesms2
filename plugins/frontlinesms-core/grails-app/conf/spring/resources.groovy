// Place your Spring DSL code here
beans = {
	multipartResolver(frontlinesms2.FrontlineMultipartResolver) {
		maxInMemorySize = application.config.upload.maximum.size
		maxUploadSize = application.config.upload.maximum.size
	}
}
