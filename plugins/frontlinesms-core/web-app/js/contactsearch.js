var contactsearch = {
	init: function(list) {
		$("#contactsearch").ajaxChosen({
			url:url_root+"search/contactSearch",
			type:"POST",
			dataType:"json",
			minTermLength:1,
			keepTypingMsg:i18n("recipientSelector.keepTyping"),
			lookingForMsg:i18n("recipientSelector.searching"),
			sendSelectedSoFarOnEachLookup:true });
	}
};

