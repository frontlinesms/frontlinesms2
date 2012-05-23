    sauce {
        baseUrl = 'http://myapp.test/core/'
        String username = System.getenv("SAUCE_ONDEMAND_USERNAME");
        String apiKey = System.getenv("SAUCE_ONDEMAND_ACCESS_KEY");
        if(username == null || apiKey == null){
            System.err.println("Sauce OnDemand credentials not set.");
        }
        DesiredCapabilities capabillities = DesiredCapabilities.chrome();
        capabillities.setCapability("name", "core");
        capabillities.setCapability("platform", Platform.LINUX);
        capabillities.setCapability("selenium-version", "2.21.0");
        driver = { new RemoteWebDriver(new URL("http://${username}:${apiKey}@ondemand.saucelabs.com:80/wd/hub"), capabillities) }
    }