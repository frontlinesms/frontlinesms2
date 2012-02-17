driver = {
	def driver = new org.openqa.selenium.firefox.FirefoxDriver()
	driver.executeScript('window.resizeTo(1366, 768)')
	return driver
}
