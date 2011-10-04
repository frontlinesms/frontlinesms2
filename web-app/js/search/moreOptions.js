$(document).ready(function() {
	//init the page javascript element
	$(".datepicker").datepicker({
		buttonImage : '../images/buttons/calendar.png',
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
