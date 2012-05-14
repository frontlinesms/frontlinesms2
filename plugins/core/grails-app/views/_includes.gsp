<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico',plugin:'grailsApplication.config.frontlinesms.plugin')}" type="image/x-icon"/>
<r:script disposition="head">
	url_root = "${request.contextPath}/";
	url = "${request.forwardURI}/";
	controller = "${params?.controller}";
	action = "${params?.action}";
	refresh_rate = ${params.rRate ?: 30000};
	grailsEnvironment = "${grails.util.GrailsUtil.environment}";

	var i18nStrings = {};
	function i18n(key) {
		var translated = i18nStrings[key];
		if(typeof(translated) == 'undefined') return key;
		for(i=arguments.length-1; i>0; --i) {
			translated = translated.replace("{"+(i-1)+"}", arguments[i]);
		}
		return translated;
	}

	<g:if env="test">
		// declare our own, non-functioning select menu and button methods so that standard HTML elements are used in tests
		$.fn.selectmenu = function() {}
		$.fn.button = function() {}
	</g:if>
	<g:else>
		$(function() {
		        // make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
		        $(".dropdown").selectmenu();
		
			setInterval(refreshSystemNotifications, 10000);
			function refreshSystemNotifications() {
				$.get("${createLink(controller:'systemNotification', action:'list')}", function(data) {
						$("#notifications").empty().append(data);
				});
			}
		});
	</g:else>
</r:script>

