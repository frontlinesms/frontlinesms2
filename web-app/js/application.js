$(function() {
	$(".dropdown").selectmenu();
	setInterval(refreshMessageCount, 30000);
	setInterval(refreshStatusIndicator, 30000);
});

function refreshMessageCount() {
	$.get(url_root + 'message/getUnreadMessageCount', function(data) {
		$('#inbox-indicator').html(data);
	});
}

function refreshStatusIndicator() {
	$.get(url_root + 'status/trafficLightIndicator', function(data) {
		$('#status-indicator').removeClass('green');
		$('#status-indicator').removeClass('red');
		$('#status-indicator').addClass(data);
	});
}

function isElementEmpty(selector) {
	return isEmpty($(selector).val());
}

function isEmpty(val) {
	return val.trim().length == 0
}

function isGroupChecked(groupName) {
	return getSelectedGroupElements(groupName).length > 0;
}

function getSelectedGroupElements(groupName) {
	return $('input[name=' + groupName + ']:checked');
}

function isDropDownSelected(id) {
	var selectedOptions = $("#" + id + " option:selected")
	return selectedOptions.length > 0  && (!isEmpty(selectedOptions[0].value))
}

$('.check-bound-text-area').live('focus', function() {
  	var checkBoxId = $(this).attr('checkbox_id');
	$('#' + checkBoxId).attr('checked', true);
});

function findInputWithValue(value) {
	return $('input[value=' + "'" + value + "'" + ']');
}

function isCheckboxSelected(value) {
	return findInputWithValue(value).is(':checked')
}

$.fn.renderDefaultText = function() {
	return this.focus( function() {
		$(this).toggleClass('default-text-input', false);
		var element = $(this).val();
		$(this).val( element === this.defaultValue ? '' : element );
	}).blur(function(){
		var element = $(this).val();
		$(this).val( element.match(/^\s+$|^$/) ? this.defaultValue : element );
		$(this).toggleClass('default-text-input', $(this).val() === this.defaultValue);
		});
};

