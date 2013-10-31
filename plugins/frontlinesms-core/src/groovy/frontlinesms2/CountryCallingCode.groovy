package frontlinesms2

public enum CountryCallingCode {
	US("1", "", 10),
	CA("1"),
	PR("1"),
	BS("1242"),
	BB("1246"),
	AI("1264"),
	AG("1268"),
	VG("1284"),
	VI("1340"),
	KY("1345"),
	BM("1441"),
	GD("1473"),
	MF("1599"),
	TC("1649"),
	MS("1664"),
	MP("1670"),
	GU("1671"),
	AS("1684"),
	LC("1758"),
	DM("1767"),
	VC("1784"),
	DO("1809"),
	TT("1868"),
	KN("1869"),
	JM("1876"),
	EG("20"),
	MA("212"),
	DZ("213"),
	TN("216"),
	LY("218"),
	GM("220"),
	SN("221"),
	MR("222"),
	ML("223"),
	GN("224"),
	CI("225"),
	BF("226"),
	NE("227"),
	TG("228"),
	BJ("229"),
	MU("230"),
	LR("231"),
	SL("232"),
	GH("233"),
	NG("234"),
	TD("235"),
	CF("236"),
	CM("237"),
	CV("238"),
	ST("239"),
	GQ("240"),
	GA("241"),
	CG("242"),
	CD("243"),
	AO("244"),
	GW("245"),
	SC("248"),
	SD("249"),
	RW("250"),
	ET("251"),
	SO("252"),
	DJ("253"),
	KE("254", "0", 9),
	TZ("255"),
	UG("256"),
	BI("257"),
	MZ("258"),
	ZM("260"),
	MG("261"),
	YT("262"),
	ZW("263"),
	NA("264"),
	MW("265"),
	LS("266"),
	BW("267"),
	SZ("268"),
	KM("269"),
	ZA("27"),
	SH("290"),
	ER("291"),
	AW("297"),
	FO("298"),
	GL("299"),
	GR("30"),
	NL("31"),
	BE("32"),
	FR("33"),
	ES("34"),
	GI("350"),
	PT("351"),
	LU("352"),
	IE("353"),
	IS("354"),
	AL("355"),
	MT("356"),
	CY("357"),
	FI("358"),
	BG("359"),
	HU("36"),
	LT("370"),
	LV("371"),
	EE("372"),
	MD("373"),
	AM("374"),
	BY("375"),
	AD("376"),
	MC("377"),
	SM("378"),
	UA("380"),
	RS("381"),
	ME("382"),
	HR("385"),
	SI("386"),
	BA("387"),
	MK("389"),
	VA("39"),
	IT("39"),
	RO("40"),
	CH("41"),
	CZ("420"),
	SK("421"),
	LI("423"),
	AT("43"),
	GB("44", "0", 10),
	JE("44"),
	IM("44"),
	DK("45"),
	SE("46"),
	NO("47"),
	PL("48"),
	DE("49"),
	FK("500"),
	BZ("501"),
	GT("502"),
	SV("503"),
	HN("504"),
	NI("505"),
	CR("506"),
	PA("507"),
	PM("508"),
	HT("509"),
	PE("51"),
	MX("52"),
	CU("53"),
	AR("54"),
	BR("55"),
	CL("56"),
	CO("57"),
	VE("58"),
	BL("590"),
	BO("591"),
	GY("592"),
	EC("593"),
	PY("595"),
	SR("597"),
	UY("598"),
	AN("599"),
	MY("60"),
	AU("61"),
	CX("61"),
	CC("61"),
	ID("62"),
	PH("63"),
	NZ("64"),
	SG("65"),
	TH("66"),
	TL("670"),
	AQ("672"),
	BN("673"),
	NR("674"),
	PG("675"),
	TO("676"),
	SB("677"),
	VU("678"),
	FJ("679"),
	PW("680"),
	WF("681"),
	CK("682"),
	NU("683"),
	WS("685"),
	KI("686"),
	NC("687"),
	TV("688"),
	PF("689"),
	TK("690"),
	FM("691"),
	MH("692"),
	RU("7"),
	KZ("7"),
	JP("81"),
	KR("82"),
	VN("84"),
	KP("850"),
	HK("852"),
	MO("853"),
	KH("855"),
	LA("856"),
	CN("86"),
	PN("870"),
	BD("880"),
	TW("886"),
	TR("90"),
	IN("91"),
	PK("92"),
	AF("93"),
	LK("94"),
	MM("95"),
	MV("960"),
	LB("961"),
	JO("962"),
	SY("963"),
	IQ("964"),
	KW("965"),
	SA("966"),
	YE("967"),
	OM("968"),
	AE("971"),
	IL("972"),
	BH("973"),
	QA("974"),
	BT("975"),
	MN("976"),
	NP("977"),
	IR("98"),
	TJ("992"),
	TM("993"),
	AZ("994"),
	GE("995"),
	KG("996"),
	UZ("998");

	private final String countryCode;
	/**
	* Trunk code for calling internally, or <code>null</code> if unknown. Value <code>0</code>
	* will be used in place of <code>null</code> values - an empty string (<code>""</code>)
	* should be used if the trunk code is known to be empty.
	*/
	private final String trunkCode;
	private final Integer length;

	//> CONSTRUCTORS
	CountryCallingCode (String countryCode) {
		this(countryCode, null, null);
	}

	CountryCallingCode(String countryCode, String trunkCode, Integer length) {
		this.countryCode = countryCode;
		this.trunkCode = trunkCode;
		this.length = length;
	}

	//> ACCESSORS
	public String getCountryCode() {
		return countryCode;
	}

	//> INSTANCE METHODS
	public boolean isValidLocalNumber(String phoneNumber) {
		return phoneNumber.matches(
						(trunkCode==null?"":trunkCode) +
						"[\\d]" + 
						(length==null?"+":"{"+length+"}"));
	}

	//> STATIC METHODS
	/**
	* @param 2-letter country ISO country code
	*/
	static String getCountryCode(String country) {
		if (country == null || country.length()==0) {
				return "";
		} else {
				CountryCallingCode code = valueOf(country.toUpperCase());
				return code == null ? "" : code.getCountryCode();
		}
	}

	/**
	* Tries to format the given phone number into a valid international format.
	* @param phoneNumber A non-formatted phone number
	*/
	public static String format(String phoneNumber, String countryCode) {
		// Remove the (0) sometimes present is certain numbers.
		// This 0 MUST NOT be present in the international formatted number
		String formattedNumber = phoneNumber.replace("(0)", "");
		
		// Remove every character which is not a digit
		formattedNumber = formattedNumber.replaceAll("\\D", "");
		
		if (phoneNumber.startsWith("+")) {
				// If the original number was prefixed by ++,
				// we put it back
				return "+" + formattedNumber;
		} else if (formattedNumber.startsWith("00")) {
				// If the number was prefixed by the (valid) 00(code) format,
				// we transform it to the + sign
				return "+" + formattedNumber.substring(2);
		} else if (formattedNumber.startsWith(getCountryCode(countryCode))) {
				// If the number was prefixed by the current country code,
				// we just put a + sign back in front of it.
				return "+" + formattedNumber;
		} else if (formattedNumber.startsWith("0")) {
				// Most internal numbers starts with one 0. We'll have to remove it
				// Before putting a + sign in front of it.
				formattedNumber = formattedNumber.substring(1);
		}
		
		// NB: even if a + sign had been specified, it's been removed by the replaceAll function
		// We have to put one back.
		// We also try to prefix the number with the current country code
		return "+" + getCountryCode(countryCode) + formattedNumber;
	}

	/**
	* @param msisdn A phone number
	* @return <code>true</code> if the number is in a proper international format, <code>false</code> otherwise.
	*/
	public static boolean isInInternationalFormat(String msisdn) {
		return msisdn.matches("\\+\\d+");
	}

	public static boolean isValidInternationalNumber(String msisdn) {
		if(msisdn == null || msisdn.length()==0 || !isInInternationalFormat(msisdn)) return false;
		msisdn = msisdn.substring(1);
		for(CountryCallingCode c : values()) {
				String code = c.getCountryCode();
				if(msisdn.startsWith(code)) {
						return c.length==null || msisdn.length()==c.length+code.length();
				}
		}
		return false;
	}

	public static boolean isValidLocalNumber(String phoneNumber, String userCountry) {
		CountryCallingCode ccc = valueOf(userCountry.toUpperCase());
		if(ccc == null) return false;
		else return ccc.isValidLocalNumber(phoneNumber);
	}

	def lookUp(rawPhoneNumber) {
		if(rawPhoneNumber.length() == 0 || rawPhoneNumber.charAt(0) != '+') {
			return null;
		}

		phoneNumber = rawPhoneNumber.substring(1);

		for(CountryCallingCode c : values()) {
			if(phoneNumber.startsWith(c.countryCode)) {
				return c;
			}
			return null;
		}
	}
}
