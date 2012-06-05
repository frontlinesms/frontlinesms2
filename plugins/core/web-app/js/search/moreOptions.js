$(function() {	
	
	$(".extra-option").each(function() {
		if (!$(this).children("input").val())
			$(this).hide();
		else
			$(".extra-option-link#" + $(this).children("input").attr('id') + "-add").hide();
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
	$(".extra-option#" + element + "-list-item").find("input").val('');
}
