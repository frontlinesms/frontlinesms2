import org.openqa.selenium.*
import org.openqa.selenium.firefox.*
import org.openqa.selenium.remote.*

def jenkins = Boolean.parseBoolean(System.properties.jenkins)

waiting {
	timeout = jenkins? 10: 5
	retryInterval = 0.2
	presets {
		slow {
			timeout = jenkins? 20: 10
			retryInterval = 1
		}
		'very slow' {
			timeout = jenkins? 30: 15
			retryInterval = 1
		}
	}
}

driver = {
	def driver = new FirefoxDriver()
	if(driver?.class?.simpleName == 'FirefoxDriver') {
		def width = 1366
		def height = 768
		driver.navigate().to('http://localhost') // make sure that the browser has opened so we can resize it
		driver.manage().window().setSize(new Dimension(width, height))
	} else if(driver?.class?.simpleName == 'HtmlUnitDriver') {
		driver.javascriptEnabled = true
		driver.@webClient.throwExceptionOnScriptError = false
	} else {
		DesiredCapabilities capabilities = new DesiredCapabilities()
		capabilities.javascriptEnabled = true
		driver = new RemoteWebDriver(new URL("http://localhost:8083"), capabilities)
		try {
			driver.navigate().to('http://localhost:8080/core') // TODO should read this url from grails settings
		} catch(TimeoutException ex) {
			sleep 30000
		}
		def width = 1366
		def height = 768
		driver.manage().window().setSize(new Dimension(width, height))
		actualCapabilities = ((RemoteWebDriver) driver).getCapabilities();
		assert actualCapabilities.javascriptEnabled
	}
	return driver
}

