$(document).ready(function() {
	if(!$("#contactSearchString").val()) {
		$("#contact-name-field").hide();
	}
	$("#expanded-search-options").hide();
});

function expandOptions() {
	$("#expanded-search-options").show();
}

function addContactField() {
	$("#contact-name-field").toggle();
	$("#expanded-search-options").hide();
}
