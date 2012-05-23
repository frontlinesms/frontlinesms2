waiting {
	timeout = 2
	retryInterval = 0.2
}

driver = {
	def useSauceLabs=Boolean.parseBoolean(System.properties['useSauceLabs']?:'false')
	def driver
	if (useSauceLabs) {
		def sauceUser=System.properties['saucelabs.username']
		def saucePass=System.properties['saucelabs.passkey']
		org.openqa.selenium.remote.DesiredCapabilities capabillities = org.openqa.selenium.remote.DesiredCapabilities.firefox();
	        capabillities.setCapability("version", "5");
	        capabillities.setCapability("platform", Platform.XP);
	        capabillities.setCapability("name", "FrontlineSMS2 Functional test cases");
	        capabillities.setCapability("selenium-version", "2.21.0")
		driver = new org.openqa.selenium.remote.RemoteWebDriver(
	           new URL("http://${sauceUser}:${saucePass}@ondemand.saucelabs.com:80/wd/hub"),
	           capabillities);
	        driver.manage().timeouts().implicitlyWait(30, java.util.concurrent.TimeUnit.SECONDS);
	}
	else {
	    driver = new org.openqa.selenium.firefox.FirefoxDriver()	
	}
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
