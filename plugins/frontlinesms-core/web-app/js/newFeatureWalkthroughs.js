function processNewTour() {

	var allGuider = {
		"autoforward": {
			"shortName": "autoforward" ,
			"friendlyName": "Autoforward" ,
			"descriptiveText": "Autoforward allows you to automatically forward messages to contacts"
		},
		"autoreply": {
			"shortName": "autoreply" ,
			"friendlyName": "Autoreply" ,
			"descriptiveText": "Autoreply allows you to automatically respond to incoming messages"
		},
		"announcement": {
			"shortName": "announcement" ,
			"friendlyName": "Announcement" ,
			"descriptiveText": "Announcement allows you to send an announcement and organise the responses"
		},
		"poll": {
			"shortName": "poll" ,
			"friendlyName": "Poll" ,
			"descriptiveText" : "Poll allows you to send a question and analyze the responses"
		},
		"subscription": {
			"shortName": "subscription" ,
			"friendlyName": "Subscription" ,
			"descriptiveText": "Subscriptions lets your end users join and leave groups using SMS"
		},
		"webconnection": {
			"shortName": "webconnection" ,
			"friendlyName": "Webconnection" ,
			"descriptiveText": "Webconnection allows you to connect to a web service as well as Crowdmap/Ushahidi"
		},
		"connection": {
			"shortName": "connection" ,
			"friendlyName": "Connection" ,
			"descriptiveText": "This tour guides you on how to make a connection with a phone/usb modem, your Clickatel or/and IntelliSms account as well as Smsync"
		}
	}
	$.each(allGuider, function(key, value) {
		if(key ==  getURLParameter('tourName')) {
			if(key == "connection")
				showConnectionWalkthrough(value.shortName, value.friendlyName, value.descriptiveText);
			else
				showActivityWalkthrough(value.shortName, value.friendlyName, value.descriptiveText);
			return
		}
	});
}

function createAndShowGuider(params) {
		guiders.hideAll();
		guiders.createGuider(params).show();
}

function newFeaturesLink (){
	guiders.hideAll();
	$.ajax({
		url: '/frontlinesms-core/help/main',
		success: function(data) {
			mediumPopup.launchHelpWizard(data);
		}
	});
}

function getURLParameter(name) {
    return decodeURI(
        (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
    );
}
function showConnectionWalkthrough(shortName, friendlyName, descriptiveText){
	createAndShowGuider({
		buttons: [{name: "Start Tour", onclick: guiders.next},{name:"Close"}],
		description: descriptiveText,
		id: "new_act_1",
		next: "new_act_2",
		overlay: true,
		title: friendlyName,
	});

	guiders.createGuider({
		attachTo: "ul#system-menu li:first-child a",
		buttons: [{name:"Next"}],
		description: "click this linkn to redirect to the connections page",
		id: "new_act_2",
		next: "new_act_3",
		position: 9,
		highlight:"ul#system-menu li:first-child a",
		overlay: true,
	});

	guiders.createGuider({
		attachTo: "div#body-menu ul li:nth-child(2)",
		buttons: [{name: "Next"}],
		description: "Click this link to open the connections page",
		id: "new_act_3",
		next: "new_act_4",
		position: 3,
		highlight: "div#body-menu ul li:nth-child(2)",
		overlay: true,
		title: "Guiders can be customized.",
	});

	guiders.createGuider({
		attachTo: 'a[name="addConnection"]',
		buttons: [{name: "Close", onclick: guiders.hideAll}],
		description: "Click this button to open the connection wizard",
		id: "new_act_4",
		position: 9,
		highlight: 'a[name="addConnection"]',
		overlay: true,
		xButton: true,
		title: "Guiders can be customized.",
	});

	$('a[name="addConnection"]').click(function(){
		guiders.hideAll();
	});
}
function showActivityWalkthrough(shortName, friendlyName, descriptiveText) {
	createAndShowGuider({
		attachTo: "#create-new-activity",
		buttons: [{name: "Close"}],
		description: "Click this button to launch the 'new activity' wizard",
		id: "new_act_2",
		next: "new_act_3",
		highlight: "#create-new-activity",
		overlay: true,
		position: 3,
		title: "Create a new "+friendlyName,
		width: 500
	});

	$(".ui-dialog").live( "dialogopen", function(event, ui) {
		if($(this).find("input#activity").is(":visible")) {
			$(this).find("input#activity").css('background', 'red');
			// select activity type popup
			createAndShowGuider({
				attachTo: "div#modalBox input[value="+shortName+"]",
				buttons: [{name: "Close"}],
				description: "",
				id: "new_act_3",
				next: "new_act_4",
				overlay: true,
				position: 12,
				title: "Choose '"+friendlyName+"'",
				width: 500
			});
		}
		else if($(this).find("div#tabs-1").is(":visible")) {
			// webconnection wizard
			createAndShowGuider({
				buttons: [{name: "Close and end Tour", onclick: guiders.hideAll }/*, {name: "To new features", onclick: newFeaturesLink }*/],
				description: "Follow the steps in the wizard to complete setup",
				id: "new_act_4",
				overlay: true,
				position: 6,
				title: "Configure your new "+friendlyName,
				width: 500
			});
			$("tabs-2").live("load", function() { console.log("test 123"); });
		}
	});
	
}