package net.frontlinesms.install4j.custom;

import java.util.HashMap;
import java.util.Map;

import com.install4j.api.context.InstallerContext;
import com.install4j.api.formcomponents.FormEnvironment;

public class FrontlineRegistration {
	public void send(FormEnvironment formEnvironment,InstallerContext context) {
		String[] prof = { "Agriculture", "Conservation", "Commercial",
				"Education", "Elections", "Emergency response", "Gender",
				"Governance", "Health", "Human rights",
				"Humanitarian Assistance", "Legal services", "Media",
				"Mobile finance", "Radio", "Other" };

		String[] countries = { "Afghanistan", "Albania", "Algeria", "Andorra",
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

		Integer var_category_of_work = (Integer) context
				.getVariable("var_category_of_work");
		String var_city_region = (String) context
				.getVariable("var_city_region");
		Integer var_country = (Integer) context.getVariable("var_country");
		boolean var_feature_in_user_map = context
				.getBooleanVariable("var_feature_in_user_map");
		boolean var_findout_more_about_research = context
				.getBooleanVariable("var_findout_more_about_research");
		boolean var_get_frontlinesms_newsletter = context
				.getBooleanVariable("var_get_frontlinesms_newsletter");
		String var_name = (String) context.getVariable("var_name");
		String var_operating_system = (String) context
				.getVariable("var_operating_system");
		String var_organization_name = (String) context
				.getVariable("var_organization_name");
		String var_share_email = (String) context
				.getVariable("var_share_email");
		String var_share_name = (String) context.getVariable("var_share_name");
		String var_share_telephone_skype = (String) context
				.getVariable("var_share_telephone_skype");
		boolean var_share_your_data = context
				.getBooleanVariable("var_share_your_data");
		String var_use = (String) context.getVariable("var_use");
		String var_website_address = (String) context
				.getVariable("var_website_address");

		Map data = new HashMap();

		data.put("var_category_of_work", "" + prof[var_category_of_work]);
		data.put("var_city_region", var_city_region);
		data.put("var_country", "" + countries[var_country]);
		data.put("var_feature_in_user_map", "" + var_feature_in_user_map);
		data.put("var_findout_more_about_research", ""
				+ var_findout_more_about_research);
		data.put("var_get_frontlinesms_newsletter", ""
				+ var_get_frontlinesms_newsletter);
		data.put("var_name", var_name);
		data.put("var_operating_system", var_operating_system);
		data.put("var_organization_name", var_organization_name);
		data.put("var_share_email", var_share_email);
		data.put("var_share_name", var_share_name);
		data.put("var_share_telephone_skype", var_share_telephone_skype);
		data.put("var_share_your_data", "" + var_share_your_data);
		data.put("var_use", var_use);
		data.put("var_website_address", var_website_address);

		Testsend ts = new Testsend();
		boolean succeeded = ts.submitData(data);

		if (succeeded) {
			formEnvironment.getFormComponentById("68").setVisible(true);
			formEnvironment.getFormComponentById("69").setVisible(true);
			formEnvironment.getFormComponentById("123").setVisible(false);
			formEnvironment.getFormComponentById("124").setVisible(false);
		} else {
			formEnvironment.getFormComponentById("123").setVisible(true);
			formEnvironment.getFormComponentById("124").setVisible(true);
			formEnvironment.getFormComponentById("68").setVisible(false);
			formEnvironment.getFormComponentById("69").setVisible(false);
		}
	}
}
