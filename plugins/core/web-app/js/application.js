// Array.indexOf is not available in IE before IE9, so
// add it here using the jQuery implementation
if(!Array.prototype.indexOf) {
	Array.prototype.indexOf = function(index) {
		return jQuery.inArray(index, this);
	};
}

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

function refreshMessageCount() {
	$.get(url_root + 'message/unreadMessageCount', function(data) {
		$('#inbox-indicator').html(data);
	});
}

function refreshStatusIndicator() {
	var updateLight = function(color) {
		$('#status-indicator').removeClass('green');
		$('#status-indicator').removeClass('red');
		$('#status-indicator').addClass(color);
		$('#status-indicator').show();
	},
	getConnectionLostNotification = function() {
		return $("#server-connection-lost-notification");
	},
	_errorHandler = function() {
		if(!getConnectionLostNotification().length) {
			var notification = '<div id="server-connection-lost-notification"><div class="content"><p>'
					+ i18n('server.connection.fail.title')
					+ '</p><p>'
					+ i18n('server.connection.fail.info')
					+ '</p></div></div>';
			$('body').append($(notification));
		}
		updateLight('red');
	},
	_successHandler = function(data) {
		var connectionLostNotification = getConnectionLostNotification();
		if(connectionLostNotification) {
			connectionLostNotification.remove();
		}
		updateLight(data);
	};

	$.ajax({
		url: url_root + 'status/trafficLightIndicator',
		success: _successHandler,
		error: _errorHandler
	});
}

function isElementEmpty(selector) {
	return isEmpty($(selector).val());
}

function isEmpty(val) {
	return val.trim().length === 0;
}

function isGroupChecked(groupName) {
	return getSelectedGroupElements(groupName).length > 0;
}

function getSelectedGroupElements(groupName) {
	return $('input[name=' + groupName + ']:checked');
}

function isDropDownSelected(id) {
	var selectedOptions = $("#" + id + " option:selected");
	return selectedOptions.length > 0  && (!isEmpty(selectedOptions[0].value));
}

$('.check-bound-text-area').live('focus', function() {
	var checkBoxId = $(this).attr('checkbox_id');
	$('#' + checkBoxId).attr('checked', true);
});

function findInputWithValue(value) {
	return $('input[value=' + "'" + value + "'" + ']');
}

function isCheckboxSelected(value) {
	return findInputWithValue(value).is(':checked');
}

$.fn.renderDefaultText = function() {
	return this.focus(function() {
			$(this).toggleClass('default-text-input', false);
			var element = $(this).val();
			$(this).val(element === this.defaultValue ? '' : element);
		}).blur(function() {
			var element = $(this).val();
			$(this).val(element.match(/^\s+$|^$/) ? this.defaultValue : element);
			$(this).toggleClass('default-text-input', $(this).val() === this.defaultValue); });
};

function showThinking() {
	$('#thinking').fadeIn();
}

function hideThinking() {
	$('#thinking').fadeOut();
}

function insertAtCaret(areaId, text) {
	var range,
	txtarea = document.getElementById(areaId),
	scrollPos = txtarea.scrollTop,
	strPos = 0,
	browser = ((txtarea.selectionStart || txtarea.selectionStart === '0') ?
			"ff" : (document.selection ? "ie" : false ) );
	if (browser === "ie") {
		txtarea.focus();
		range = document.selection.createRange();
		range.moveStart ('character', -txtarea.value.length);
		strPos = range.text.length;
	} else if (browser === "ff") {
		strPos = txtarea.selectionStart;
	}

	var front = (txtarea.value).substring(0, strPos);
	var back = (txtarea.value).substring(strPos, txtarea.value.length);
	txtarea.value=front + text + back;
	strPos = strPos + text.length;
	if (browser === "ie") {
		txtarea.focus();
		range = document.selection.createRange();
		range.moveStart ('character', -txtarea.value.length);
		range.moveStart ('character', strPos);
		range.moveEnd ('character', 0);
		range.select();
	} else if (browser === "ff") {
		txtarea.selectionStart = strPos;
		txtarea.selectionEnd = strPos;
		txtarea.focus();
	}
	txtarea.scrollTop = scrollPos;
}

$(function() {
	setInterval(refreshMessageCount, 30000);
	setInterval(refreshStatusIndicator, 30000);
	refreshStatusIndicator();
});

