$(document).ready(function() {
	//init the page javascript element
	$(".datepicker").datepicker({
		buttonImage : '../images/icons/calendar.png',
		buttonImageOnly: true,
	    showOn: 'both',
	    onSelect: function (dateText, inst) {
	    	var date = new Date(dateText)
	    	$($(this).parent().children('select').get(0)).val(date.getDate());
	    	$($(this).parent().children('select').get(1)).val(date.getMonth()+1);
	    	$($(this).parent().children('select').get(2)).val(date.getFullYear())
	    }
		});
	
	
	//hide the more options fields
	$("[id^=more-option-field]").each(function(){
		if (!$(this).children("input").val()) {
			$(this).hide()
		} else {
			$("#"+this.id.replace("more-option-field","more-option-link")).hide();
		}
	})
	$("#expanded-search-options").hide();
	$("#extra-options").live("click",expandOptions)
});

function expandOptions() {
	$("#expanded-search-options").show();
	$("#extra-options").die("click").live("click",hideOptions)
}

function hideOptions() {
	$("#expanded-search-options").hide();
	$("#extra-options").die("click").live("click",expandOptions)
}


function toggleExtraSearchOption(option) {
	$("#extra-option-field-" + option).toggle();
	$("#extra-option-link-" + option).toggle();
	if (!$("#extra-option-field-" + option).is(":visible")) {
		$("#extra-option-field-" + option).children("input").val("")
	}
	hideOptions();
}
