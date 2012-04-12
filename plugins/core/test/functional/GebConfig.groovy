driver = {
	def driver = new org.openqa.selenium.firefox.FirefoxDriver()
//	def driver = new org.openqa.selenium.htmlunit.HtmlUnitDriver()
	if(driver.class.simpleName == 'FirefoxDriver') {
		def width = 1366
		def height = 768
		driver.navigate().to('http://localhost')
		driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height))
	} else if(driver.class.simpleName == 'HtmlUnitDriver') {
		driver.javascriptEnabled = true
		driver.@webClient.throwExceptionOnScriptError = false
	}
	return driver
}
