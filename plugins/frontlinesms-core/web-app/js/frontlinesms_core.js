// Array.indexOf is not available in IE before IE9, so
// add it here using the jQuery implementation
if(!Array.prototype.indexOf) {
	Array.prototype.indexOf = function(index) {
		return jQuery.inArray(index, this);
	};
}

if(!Array.prototype.remove) {
	Array.prototype.remove = function(obj) {
		var i;
		while((i = this.indexOf(obj)) !== -1) {
			this.splice(i, 1);
		}
	}
}

// String.trim is not available in IE before IE9, so
// add it here using the jQuery implementation
if(!String.prototype.trim) {
	String.prototype.trim = function() {
		return jQuery.trim(this);
	};
}

if(!String.prototype.startsWith) {
	String.prototype.startsWith = function(prefix) {
		return this.slice(0, prefix.length) === prefix;
	};
}

if(!String.prototype.endsWith) {
	String.prototype.endsWith = function(suffix) {
		return this.indexOf(suffix, this.length - suffix.length) !== -1;
	};
}

if(!String.prototype.htmlEncode) {
	String.prototype.htmlEncode = function() {
		return this
			.replace(/&/g, '&amp;')
			.replace(/"/g, '&quot;')
			.replace(/'/g, '&#39;')
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;');
	};
}

//Imported from http://werxltd.com/wp/2010/05/13/javascript-implementation-of-javas-string-hashcode-method/
if(!String.prototype.hashCode) {
	String.prototype.hashCode = function() {
		var hash = 0, i, character;
		if (this.length == 0) return hash;
		for (i = 0, l = this.length; i < l; i++) {
			character  = this.charCodeAt(i);
			hash  = ((hash<<5)-hash)+character;
			hash |= 0;
		}
		return hash;
	};
}

// Standardise the onclick/onchange firing in IE before IE9
function addChangeHandlersForRadiosAndCheckboxes() {
	jQuery('input:radio, input:checkbox').click(function() {
		this.blur();
		this.focus();
	});
}
if(jQuery.browser.msie) { jQuery(function() {
	addChangeHandlersForRadiosAndCheckboxes();
});}

(function(jQuery) {
	jQuery.fn.disableField = function(){
	    return this.each(function(){
	        this.disabled = true;
	    });
	};
	jQuery.fn.enableField = function(){
	    return this.each(function(){
	        this.disabled = false;
	    });
	};
}(jQuery));

function getSelectedGroupElements(groupName) {
	return jQuery('input[name=' + groupName + ']:checked');
}

function isGroupChecked(groupName) {
	return getSelectedGroupElements(groupName).length > 0;
}

jQuery('.check-bound-text-area').live('focus', function() {
	var checkBoxId = jQuery(this).attr('checkbox_id');
	jQuery('#' + checkBoxId).attr('checked', true);
});

jQuery.fn.renderDefaultText = function() {
	return this.focus(function() {
			jQuery(this).toggleClass('default-text-input', false);
			var element = jQuery(this).val();
			jQuery(this).val(element === this.defaultValue ? '' : element);
		}).blur(function() {
			var element = jQuery(this).val();
			jQuery(this).val(element.match(/^\s+$|^$/) ? this.defaultValue : element);
			jQuery(this).toggleClass('default-text-input', jQuery(this).val() === this.defaultValue); });
};

function showThinking() {
	jQuery('#thinking').show();
}

function fadeThinking() {
	jQuery('#thinking').fadeOut();
}

function hideThinking() {
	jQuery('#thinking').hide();
}

function insertAtCaret(areaId, text) {
	var front, back, range,
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

	front = (txtarea.value).substring(0, strPos);
	back = (txtarea.value).substring(strPos, txtarea.value.length);
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

jQuery(document).ajaxError(function(request, data, settings, error) {
	var title;
	// Ignore errors with AppInfo as they should already be handled.  If status is zero, the
	// server is likely down, or an auth error.  AppInfo should update this page anyway shortly
	if(!settings.url.match(/^.*\/appInfo$/) && data.status !== 0) {
		// remove loading screen just in case it is there
		hideThinking();
		// display details of the error page
		launchSmallPopup(data.status + ": " + data.statusText, data.responseText, i18n("action.ok"), cancel);
	}
});

jQuery(function() {
	if(!jQuery.validator) {
		return;
	}
	jQuery.extend(jQuery.validator.messages, {
		required: i18n("jquery.validation.required"),
		remote: i18n("jquery.validation.remote"),
		email: i18n("jquery.validation.email"),
		url: i18n("jquery.validation.url"),
		date: i18n("jquery.validation.date"),
		dateISO: i18n("jquery.validation.dateISO"),
		number: i18n("jquery.validation.number"),
		digits: i18n("jquery.validation.digits"),
		creditcard: i18n("jquery.validation.creditcard"),
		equalTo: i18n("jquery.validation.equalto"),
		accept: i18n("jquery.validation.accept"),
		maxlength: jQuery.validator.format(i18n("jquery.validation.maxlength")),
		minlength: jQuery.validator.format(i18n("jquery.validation.minlength")),
		rangelength: jQuery.validator.format(i18n("jquery.validation.rangelength")),
		range: jQuery.validator.format(i18n("jquery.validation.range")),
		max: jQuery.validator.format(i18n("jquery.validation.max")),
		min: jQuery.validator.format(i18n("jquery.validation.min"))
	});

	jQuery.fn.showIf = function(shouldShow) {
		var args = Array.prototype.slice.call(arguments, 1);
		if(shouldShow) {
			jQuery.fn.show.apply(this, args);
		} else {
			jQuery.fn.hide.apply(this, args);
		}
	};
});

