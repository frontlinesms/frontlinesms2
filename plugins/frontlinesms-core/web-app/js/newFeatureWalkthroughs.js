function processNewTour() {
	if(getURLParameter('tourEnabled') === 'true' && getURLParameter('tourName') === 'subscription') {
		showActivityWalkthrough('subscription', 'Subscription', 'Subscriptions let your end users join and leave groups using SMS');
	}
}

function createAndShowGuider(params) {
		guiders.hideAll();
		guiders.createGuider(params).show();
}

function getURLParameter(name) {
    return decodeURI(
        (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
    );
}

function showActivityWalkthrough(shortName, friendlyName, descriptiveText) {
	createAndShowGuider({
		buttons: [{name: "Start Tour", onclick: guiders.next},{name:"Close"}],
		description: descriptiveText,
		id: "new_act_1",
		next: "new_act_2",
		overlay: true,
		title: friendlyName,
	});

	guiders.createGuider({
		attachTo: "#create-new-activity",
		buttons: [{name: "Close"}],
		description: "Launch the 'new activity' wizard",
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
				buttons: [{name: "Close and end Tour"}, {name: "Continue Editing"}],
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