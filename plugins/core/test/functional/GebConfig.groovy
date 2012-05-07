driver = {
	//def driver = new org.openqa.selenium.firefox.FirefoxDriver()
//	def driver = new org.openqa.selenium.htmlunit.HtmlUnitDriver()

	org.openqa.selenium.remote.DesiredCapabilities capabillities = org.openqa.selenium.remote.DesiredCapabilities.firefox();
        capabillities.setCapability("version", "5");
        capabillities.setCapability("platform", Platform.XP);
        capabillities.setCapability("name", "FrontlineSMS2 Functional test cases");
	def driver = new org.openqa.selenium.remote.RemoteWebDriver(
           new URL("http://sitati:6561dd21-0371-4e43-b1c9-4d86dc824061@ondemand.saucelabs.com:80/wd/hub"),
           capabillities);
        driver.manage().timeouts().implicitlyWait(30, java.util.concurrent.TimeUnit.SECONDS);
	if(driver.class.simpleName == 'FirefoxDriver') {
		def width = 1366
		def height = 768
		driver.navigate().to('http://myapp.test')
		driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height))
	} else if(driver.class.simpleName == 'HtmlUnitDriver') {
		driver.javascriptEnabled = true
		driver.@webClient.throwExceptionOnScriptError = false
	}
	return driver
}
