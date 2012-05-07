driver = {
	//def driver = new org.openqa.selenium.firefox.FirefoxDriver()
//	def driver = new org.openqa.selenium.htmlunit.HtmlUnitDriver()
	DesiredCapabilities capabillities = DesiredCapabilities.firefox();
        capabillities.setCapability("version", "5");
        capabillities.setCapability("platform", Platform.XP);
        capabillities.setCapability("name", "Testing Selenium 2 with Java on Sauce");
	def driver = new RemoteWebDriver(
           new URL("http://username-string:access-key-string@ondemand.saucelabs.com:80/wd/hub"),
           capabillities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
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
