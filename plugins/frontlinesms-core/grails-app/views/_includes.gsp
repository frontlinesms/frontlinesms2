<link rel="shortcut icon" href="${r.resource(dir:'images', file:'favicon.ico')}" type="image/x-icon"/>
<!--[if IE 8]>
	<link href="${r.resource(dir:'css', file:'ie8.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
<![endif]-->
<!--[if IE 7]>
	<link href="${r.resource(dir:'css', file:'ie7.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
<![endif]-->

<r:script disposition="head">
	var url_root = url_root || "${request.contextPath}/";
	url = "${request.forwardURI}/";
	controller = "${params?.controller}";
	action = "${params?.action}";
	refresh_rate = ${params.rRate ?: 30000};
	grailsEnvironment = "${grails.util.GrailsUtil.environment}";
	var console = console || {};
	console.log = console.log || function() {};
	var fsms_config = {
		"mobileNumbers.international.warn":${grailsApplication.config.mobileNumbers.international.warn},
		"mobileNumbers.nonNumeric.warn":      ${grailsApplication.config.mobileNumbers.nonNumeric.warn}
	},
	fsms_settings = {
		"non.numeric.characters.removed.warning.disabled": ${Boolean.parseBoolean(grailsApplication.mainContext.appSettingsService.get('non.numeric.characters.removed.warning.disabled'))},
		"international.number.format.warning.disabled": ${Boolean.parseBoolean(grailsApplication.mainContext.appSettingsService.get('international.number.format.warning.disabled'))}
	};

	preloadImage = function(imageSource) {
		new Image().src = imageSource;
	}
	preloadImage("${r.resource(dir:'images', file:'status/red.png')}");
	preloadImage("${r.resource(dir:'images', file:'chosen-sprite.png')}");


	<fsms:render template="/i18n"/>

	<g:if env="test">
		app_info.init(3000);
	</g:if>
	<g:else>
		app_info.init();
	</g:else>
	var systemNotification = new SystemNotification();
	var statusIndicator = new StatusIndicator();
	// declare vars that are populated in JS files
	var check_list, fconnection;

	app_info.listen("inbox_unread", function(data) {
		data = data.inbox_unread;
		if(!data) { return; }
		$('#inbox-indicator').html(data);
		$('#inbox-indicator').removeClass('allRead');
	});


	<g:if env="test">
		// declare our own, non-functioning select menu and button methods so that standard HTML elements are used in tests
		$.fn.selectmenu = function() {};
		selectmenuTools.snapback = function() {};
		var fsmsButton = { apply:function(){}, findAndApply:function(){}, find:function(){} };
	</g:if>
	<g:else>
		var fsmsButton = new FsmsButton();
		$(function() {
			// make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
			selectmenuTools.initAll("select");
			fsmsButton.findAndApply("input[type='submit'], input[type='button']");

			// Enable system notification refresh
			systemNotification.init();
			statusIndicator.init();
		});
	</g:else>

	<fsms:ifAppSetting test="newfeatures.popup.show.immediately">
		$(function() {
			newFeatures.showPopup();
		});
	</fsms:ifAppSetting>

	// add moreOptions js to dropdowns
	$(function() {
		if(typeof more_actions !== 'undefined') {
			more_actions.init();
		}
	});
</r:script>

