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

		var systemNotification = {
			_getId: function(e) {
				return e.attr("id").substring(13);
			},
			_create: function(id, text) {
				var elementId = "notification-" + id;
				return '<div class="system-notification" id="' + elementId + '">'
						+ text
						+ '<a onclick="systemNotification.hide(' + id + ')" class="hider">x</a></div>';
			},
			hide: function(id) {
				// mark as read with AJAX
				var link = url_root + 'systemNotification/markRead/' + id;
				$.get(link);
				// hide notification
				$("#notification-" + id).slideUp(500);
			},
			refresh: function() {
				$.get("${createLink(controller:'systemNotification', action:'list')}", function(data) {
					// remove any notifications no longer in the list
					var found = [];
					$(".system-notification").each(function(i, e) {
						e = $(e);
						var notificationId = systemNotification._getId(e);
						if(!data[notificationId]) {
							// remove dead notification
							e.slideUp(500);
						} else {
							// prevent the notification being re-added
							data[notificationId] = null;
						}
					});

					// add any new notifications to the bottom of the list
					for(key in data) {
						var value = data[key];
						if(value) {
							$("#notifications").append(systemNotification._create(key, value));
						}
					}
				});
			},
		};

		$(function() {
		        // make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
		        $(".dropdown").selectmenu();
			$("input[type='submit']").each(function() { fsmsButton.apply(this); });

			// Enable system notification refresh
			setInterval(systemNotification.refresh, 10000);
		});
	</g:else>
</r:script>

