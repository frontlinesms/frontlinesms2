var contactsearch = {
	init: function(list) {
		$("#contactsearch").ajaxChosen({ type:"POST", url:url_root+"search/contactSearch", dataType:"json" });
	}
};

