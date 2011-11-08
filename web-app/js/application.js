$(function() {
	$('#tab-messages').everyTime(refresh_rate || '30s', "refreshCountTimer", refreshMessageCount);
});

function refreshMessageCount() {
	$.get(url_root + 'message/getUnreadMessageCount', function(data) {
		$('#tab-messages').html("Messages " + data);
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
