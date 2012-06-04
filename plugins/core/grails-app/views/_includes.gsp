<link rel="shortcut icon" href="${resource(dir:'images', file:'favicon.ico')}" type="image/x-icon"/>
<!--[if IE]>
	<link href="${resource(dir:'css', file:'ie.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
<![endif]-->

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

	var systemNotification = new SystemNotification();

	<g:if env="test">
		// declare our own, non-functioning select menu and button methods so that standard HTML elements are used in tests
		$.fn.selectmenu = function() {};
		var fsmsButton = { apply: function(original) {} };
	</g:if>
	<g:else>
		var fsmsButton = {
			trigger: function() {
				// Trigger clicking of the button when the anchor is clicked.
				$(this).prev().click();
			},
			apply: function(original) {
				// replace a button with an anchor
				// find the original text
				original = $(original);
				if(original.hasClass("fsms-button-replaced")) return;
				original.addClass("fsms-button-replaced");
				var buttonText = original.val();
				var classes = original.attr("class");

				// create the new control
				var newController = $('<a class="' + classes + '">' + buttonText + '</a>');
				newController.click(fsmsButton.trigger);

				// add the new control next to original
				original.after(newController);

				// hide the current control
				original.hide();
			}
		};

		$(function() {
		        // make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
			// TODO reintroduce dropdown when the CSS is fixed
		        //$(".dropdown").selectmenu();
			$("input[type='submit']").each(function() { fsmsButton.apply(this); });

			// Enable system notification refresh
			setInterval(systemNotification.refresh, 10000);
		});
	</g:else>
</r:script>

