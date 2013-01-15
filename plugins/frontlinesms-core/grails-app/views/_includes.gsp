<link rel="shortcut icon" href="${resource(dir:'images', file:'favicon.ico')}" type="image/x-icon"/>
<!--[if IE 8]>
	<link href="${resource(dir:'css', file:'ie8.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
<![endif]-->
<!--[if IE 7]>
	<link href="${resource(dir:'css', file:'ie7.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
<![endif]-->

<r:script disposition="head">
	var url_root = url_root || "${request.contextPath}/";
	url = "${request.forwardURI}/";
	controller = "${params?.controller}";
	action = "${params?.action}";
	refresh_rate = ${params.rRate ?: 30000};
	grailsEnvironment = "${grails.util.GrailsUtil.environment}";
	var i18nStrings = i18nStrings || {};
	var console = console || {};
	console.log = console.log || function() {};
	function i18n(key) {
		var translated =
			<g:each var="plugin" in="${grailsApplication.config.frontlinesms.plugins}">
				(typeof(i18nStrings["${plugin}"])!=="undefined"? i18nStrings["${plugin}"][key]: null) ||
			</g:each>
				key;
		if(typeof(translated) == 'undefined') return key; // FIXME this line looks unnecessary
		for(i=arguments.length-1; i>0; --i) {
			translated = translated.replace("{"+(i-1)+"}", arguments[i]);
		}
		return translated;
	}

	var systemNotification = new SystemNotification();
	var statusIndicator = new StatusIndicator();
	// declare vars that are populated in JS files
	var check_list;

	<g:if env="test">
		// declare our own, non-functioning select menu and button methods so that standard HTML elements are used in tests
		$.fn.selectmenu = function() {};
		var fsmsButton = { apply:function(){}, findAndApply:function(){}, find:function(){} };
	</g:if>
	<g:else>
		var fsmsButton = new FsmsButton();
		$(function() {
			// make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
			// TODO reintroduce dropdown when the CSS is fixed
			selectmenuTools.initAll("select");
			fsmsButton.findAndApply("input[type='submit'], input[type='button']");

			// Enable system notification refresh
			setInterval(systemNotification.refresh, 10000);
			setInterval(statusIndicator.refresh, 10000);
		});
	</g:else>

	<fsms:ifAppSetting test="newfeatures.popup.show.immediately">
		$(function() {
			newFeatures.showPopup();
		});
	</fsms:ifAppSetting>
</r:script>


