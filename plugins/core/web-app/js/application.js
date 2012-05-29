(function($) {
	$.fn.disableField = function(){
	    return this.each(function(){
	        this.disabled = true;
	    });
	};
	$.fn.enableField = function(){
	    return this.each(function(){
	        this.disabled = false;
	    });
	};
})(jQuery);

$(function() {
	setInterval(refreshMessageCount, 30000);
	setInterval(refreshStatusIndicator, 30000);
	refreshStatusIndicator();
});

function refreshMessageCount() {
	$.get(url_root + 'message/unreadMessageCount', function(data) {
		$('#inbox-indicator').html(data);
	});
}

function refreshStatusIndicator() {
	$.get(url_root + 'status/trafficLightIndicator', function(data) {
		$('#status-indicator').removeClass('green');
		$('#status-indicator').removeClass('red');
		$('#status-indicator').addClass(data);
		$('#status-indicator').show();
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

function showThinking() {
	$('#thinking').fadeIn();
}

function hideThinking() {
	$('#thinking').fadeOut();
}

function insertAtCaret(areaId, text) {
	var txtarea = document.getElementById(areaId);
	var scrollPos = txtarea.scrollTop;
	var strPos = 0;
	var browser = ((txtarea.selectionStart || txtarea.selectionStart == '0') ?
			"ff" : (document.selection ? "ie" : false ) );
	if (browser == "ie") {
		txtarea.focus();
		var range = document.selection.createRange();
		range.moveStart ('character', -txtarea.value.length);
		strPos = range.text.length;
	} else if (browser == "ff") strPos = txtarea.selectionStart;

	var front = (txtarea.value).substring(0, strPos);
	var back = (txtarea.value).substring(strPos, txtarea.value.length);
	txtarea.value=front + text + back;
	strPos = strPos + text.length;
	if (browser == "ie") {
		txtarea.focus();
		var range = document.selection.createRange();
		range.moveStart ('character', -txtarea.value.length);
		range.moveStart ('character', strPos);
		range.moveEnd ('character', 0);
		range.select();
	} else if (browser == "ff") {
		txtarea.selectionStart = strPos;
		txtarea.selectionEnd = strPos;
		txtarea.focus();
	}
	txtarea.scrollTop = scrollPos;
}

