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
	
	
	$(".extra-option").each(function() {
		if (!$(this).children("input").val())
			$(this).hide()
		else
			$(".extra-option-link#" + $(this).attr('id')).hide();
	})
	$("#extra-options-list").hide();
	$("#toggle-extra-options").find("#minus").hide();
	$("#toggle-extra-options").live("click", function() {
		$("#extra-options-list").toggle();
		$("#toggle-extra-options").find("#minus").toggle();
		$("#toggle-extra-options").find("#plus").toggle();
	});
});

function toggleExtraSearchOption(option) {
	$(".extra-option#" + option + "-list-item").toggle();
	$(".extra-option-link#" + option + "-add").toggle();
}

function removeValue(element) {
	$(".extra-option#" + element + "-list-item").find("input").val("");
}
