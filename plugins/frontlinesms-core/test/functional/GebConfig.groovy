import org.openqa.selenium.*
import org.openqa.selenium.firefox.*
import org.openqa.selenium.remote.*

def jenkins = Boolean.parseBoolean(System.properties.jenkins)
baseUrl = System.properties['geb.build.baseUrl']

waiting {
	timeout = jenkins? 20: 5
	retryInterval = 0.2
	presets {
		slow {
			timeout = jenkins? 40: 10
			retryInterval = 0.2
		}
		veryslow {
			timeout = jenkins? 60: 15
			retryInterval = 0.2
		}
		'very slow' {
			timeout = jenkins? 60: 15
			retryInterval = 0.2
		}
		'very-slow' {
			timeout = jenkins? 60: 15
			retryInterval = 0.2
		}
		'very_slow' {
			timeout = jenkins? 60: 15
			retryInterval = 0.2
		}
	}
}

driver = {
	def driver = System.properties['frontlinesms2.test.remote']? null: new FirefoxDriver()
	if(driver?.class?.simpleName == 'FirefoxDriver') {
	} else if(driver?.class?.simpleName == 'HtmlUnitDriver') {
		driver.javascriptEnabled = true
		driver.@webClient.throwExceptionOnScriptError = false
	} else {
		DesiredCapabilities capabilities = new DesiredCapabilities()
		capabilities.javascriptEnabled = true
		driver = new RemoteWebDriver(new URL(baseUrl), capabilities)
		actualCapabilities = ((RemoteWebDriver) driver).getCapabilities();
		assert actualCapabilities.javascriptEnabled
	}

	println "Configured WebDriver: ${driver.class}"

	def width = 1366
	def height = 768
	driver.manage().window().setSize(new Dimension(width, height))

	return driver
}

