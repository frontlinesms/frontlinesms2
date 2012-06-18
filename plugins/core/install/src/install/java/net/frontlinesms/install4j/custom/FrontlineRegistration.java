package net.frontlinesms.install4j.custom;

import java.util.HashMap;
import java.util.Map;

import com.install4j.api.context.InstallerContext;
import com.install4j.api.formcomponents.FormEnvironment;

public class FrontlineRegistration {
	private static final String[] PROFESSION = { "Agriculture",
			"Conservation", "Commercial",
			"Education", "Elections", "Emergency response", "Gender",
			"Governance", "Health", "Human rights",
			"Humanitarian Assistance", "Legal services", "Media",
			"Mobile finance", "Radio", "Other" };

	private static final String[] COUNTRIES  = { "Afghanistan", "Albania", "Algeria", "Andorra",
			"Angola", "Antigua & Deps", "Argentina", "Armenia",
			"Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain",
			"Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
			"Benin", "Bhutan", "Bolivia", "Bosnia Herzegovina", "Botswana",
			"Brazil", "Brunei", "Bulgaria", "Burkina", "Burundi",
			"Cambodia", "Cameroon", "Canada", "Cape Verde",
			"Central African Rep", "Chad", "Chile", "China", "Colombia",
			"Comoros", "Congo", "Congo {Democratic Rep}", "Costa Rica",
			"Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark",
			"Djibouti", "Dominica", "Dominican Republic", "East Timor",
			"Ecuador", "Egypt", "El Salvador", "Equatorial Guinea",
			"Eritrea", "Estonia", "Ethiopia", "Fiji", "Finland", "France",
			"Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece",
			"Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",
			"Haiti", "Honduras", "Hungary", "Iceland", "India",
			"Indonesia", "Iran", "Iraq", "Ireland {Republic}", "Israel",
			"Italy", "Ivory Coast", "Jamaica", "Japan", "Jordan",
			"Kazakhstan", "Kenya", "Kiribati", "Korea North",
			"Korea South", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos",
			"Latvia", "Lebanon", "Lesotho", "Liberia", "Libya",
			"Liechtenstein", "Lithuania", "Luxembourg", "Macedonia",
			"Madagascar", "Malawi", "Malaysia", "Maldives", "Mali",
			"Malta", "Marshall Islands", "Mauritania", "Mauritius",
			"Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
			"Montenegro", "Morocco", "Mozambique", "Myanmar, {Burma}",
			"Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand",
			"Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan",
			"Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
			"Philippines", "Poland", "Portugal", "Qatar", "Romania",
			"Russian Federation", "Rwanda", "St Kitts & Nevis", "St Lucia",
			"Saint Vincent & the Grenadines", "Samoa", "San Marino",
			"Sao Tome & Principe", "Saudi Arabia", "Senegal", "Serbia",
			"Seychelles", "Sierra Leone", "Singapore", "Slovakia",
			"Slovenia", "Solomon Islands", "Somalia", "South Africa",
			"South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname",
			"Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan",
			"Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga",
			"Trinidad & Tobago", "Tunisia", "Turkey", "Turkmenistan",
			"Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
			"United Kingdom", "United States", "Uruguay", "Uzbekistan",
			"Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Yemen",
			"Zambia", "Zimbabwe" };
	
	public Map getRegistrationData(InstallerContext context){
		Map data = new HashMap();
		copyStrings(context, data,
				"var_name",
				"var_use",
				"var_website_address",
				"var_city_region",
				"var_organization_name",
				"var_share_email",
				"var_share_telephone_skype");
		copyBooleans(context, data,
				"var_feature_in_user_map",
				"var_findout_more_about_research",
				"var_get_frontlinesms_newsletter",
				"var_share_your_data",
				"var_monitor_impact",
				"var_partner",
				"var_share_limited_technical_info");		
		copyIntegers(context, data,
				PROFESSION, "var_category_of_work",
				COUNTRIES, "var_country");
		return addSystemPropertiesToRegistrationData(data);
	}

	private Map addSystemPropertiesToRegistrationData(Map<String, String> data) {
		data = addSystemProperties(data,
				"java.home", 
				"java.vendor",
				"java.vendor.url",
				"java.version",
				"java.vm.version",
				"java.runtime.version",
				"java.specification.version",
				"os.arch",
				"os.name",
				"os.version",
				"user.dir",
				"user.home",
				"user.name",
				"user.language",
				"user.country");
		try{
			data.put("hostname",java.net.InetAddress.getLocalHost().getHostName());
		}catch(java.net.UnknownHostException e){
			e.printStackTrace();
		}
		return data;
		}
	
	private Map addSystemProperties(Map<String, String> data, String... keys){
		for(String key: keys){
			data.put(key, System.getProperty(key));			
		}
		return data;
	}

	public void send(FormEnvironment formEnvironment, InstallerContext context) {
		Testsend ts = new Testsend();
		boolean succeeded = ts.submitData(getRegistrationData(context));

		setVisible(formEnvironment, 199, succeeded);
		setVisible(formEnvironment, 201, !succeeded);
		setVisible(formEnvironment, 202, !succeeded);
	}

	private void copyStrings(InstallerContext from, Map to, String... keys) {
		for(String key: keys) {
			copyString(from, to, key);
		}
	}

	private void copyString(InstallerContext from, Map to, String key) {
		String value = (String) from.getVariable(key);
		to.put(key, value.replace("\n", " "));
	}

	private void copyBooleans(InstallerContext from, Map to, String... keys) {
		for(String key: keys) {
			copyBoolean(from, to, key);
		}
	}

	private void copyBoolean(InstallerContext from, Map to, String key) {
		boolean value = from.getBooleanVariable(key);
		to.put(key, Boolean.toString(value));
	}

	private void copyIntegers(InstallerContext from, Map to, Object... keys) {
		for(int x=0; x<keys.length; x+=2) {
			copyInteger(from, to, (String[])keys[x], (String)keys[x+1]);
		}
	}

	private void copyInteger(InstallerContext from, Map to, String[] map, String key) {
		Integer index = (Integer) from.getVariable(key);
		String value = index==null? "": map[index];
		to.put(key, value);
	}

	private void setVisible(FormEnvironment formEnvironment, int id, boolean visible) {
		formEnvironment.getFormComponentById(Integer.toString(id)).setVisible(visible);
	}
}

