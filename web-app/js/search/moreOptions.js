$(document).ready(function() {
	$("[id^=more-option-field]").each(function(){
		if (!$(this).children("input").val()) {
			$(this).hide()
		} else {
			$("#"+this.id.replace("more-option-field","more-option-link")).hide();
		}
	})
	$("#expanded-search-options").hide();
	$("#more-search-options").live("click",expandOptions)
});

function expandOptions() {
	$("#expanded-search-options").show();
	$("#more-search-options").die("click").live("click",hideOptions)
}

function hideOptions() {
	$("#expanded-search-options").hide();
	$("#more-search-options").die("click").live("click",expandOptions)
}


function toggleMoreOptionElement(id) {
	$("#more-option-field-"+id).toggle();
	$("#more-option-link-"+id).toggle();
	if (!$("#more-option-field-"+id).is(":visible")) {
		$("#more-option-field-"+id).children("input").val("")
	}
	hideOptions();
}
