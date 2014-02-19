// Place your Spring DSL code here
beans = {
	multipartResolver(org.springframework.web.multipart.commons.CommonsMultipartResolver) {
		maxInMemorySize = 15728640
		maxUploadSize = 15728640
	}
}
