fconnection_list = (function() {
	var update = function(status, id) {
		var containerSelecter = "#connection-" + id;
		$(containerSelecter + " .connection-status").attr("class", "connection-status " + status);
		sanchez.replaceContent(containerSelecter + " .controls", "fconnection-controls-" + status, { connectionId:id });
	},
	pulseNewConnections = function(ids) {
		$.each(ids.split(','), function(index, id) {
			$('#connection-' + id).pulse({
				backgroundColor : '#5cb85c',
				color           : 'white'
			},
			{
				returnDelay : 900,
				interval    : 700,
				pulses      : 1
			});
		});
	};
	return {
		update:update,
		pulseNewConnections:pulseNewConnections
	};
}());

fconnection = (function() {
	var attachCheckBoxListener, handleSaveResponse, init, isValid, show, validator;

	isFieldValid = function(fieldName) {
		var val = getFieldVal(fieldName);
		if(typeof(val) === "boolean") {
			if(val && isSubsection(fieldName)) {
				return validateSubsectionFields(fieldName);
			}
		} else {
			return !getField(fieldName).hasClass("error") && isFieldSet(fieldName);
		}
	};

	isFieldSet = function(fieldName) {
		var val = getFieldVal(fieldName);
		return val!==null && val.length>0;
	};

	isSubsection = function(fieldName) {
		if(new RegExp(/info\-.*/).test(fieldName)) { return false; }
		return $('#' + fieldName + '-subsection').length > 0;
	};

	disableSubsectionFields = function(field) {
		var subSectionFields = $('.' + field + '-subsection-member');
		$.each(subSectionFields, function(index, value) {
			$(value).disableField();
		});
	};

	enableSubsectionFields = function(field) {
		var subSectionFields = $('.' + field + '-subsection-member');
		$.each(subSectionFields, function(index, value) {
			$(value).enableField();
		});
	};

	getFieldVal = function(fieldName) {
		var field = getField(fieldName);
		if(field.attr("type") === "checkbox") {
			return field.prop("checked");
		}
		return field.val();
	};

	getField = function(fieldName) {
		return $('#' + fconnection.getType() + fieldName);
	};

	setConfirmVal = function(fieldName, val) {
		var text, isCheckbox = $('#' + fconnection.getType() + fieldName).attr("type") === "checkbox";
		
		if(isCheckbox) {
			text = val? "Yes": "No"; // TODO i18n
			$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).text(text);
		} else if($('#' + fconnection.getType() + fieldName).is(":disabled") === false) {
			$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).parent().removeClass("hide");
			$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).text(val);
		} else {
			$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).parent().addClass("hide");
		}
	};

	setConfirmation = function(fieldName) {
		setConfirmVal(fieldName, getFieldVal(fieldName));
	};

	setSecretConfirmation = function(fieldName) {
		var val = isFieldSet(fieldName)? '****': 'None'; // FIXME i18n
		setConfirmVal(fieldName, val);
	};

	attachCheckBoxListener = function() {
		$("input[type='checkbox']").bind("change", function() {
			var key = $(this).attr("field");
			toggleSubFields(key);
		});
	};
	
	validator = function() { return $("#connectionForm").validate({ errorContainer: ".error-panel"}); };
	init = function() {
		var keys = fconnection[fconnection.getType()].validationSubsectionFieldKeys;
		$.each(keys, function(index, value) {
			toggleSubFields(value);
		});
		connectionTooltips.init(fconnection.getType());
	};
	show = function() {
		setConfirmVal('type', humanReadableName());
		setConfirmation('name');
		fconnection[fconnection.getType()].show();
	};
	isValid = function() {
		var fields, valid;
		valid = true;
		fields = $('input:enabled:visible.required');
		$.each(fields, function(index, value) {
			if (!validator().element(value) && valid) {
				valid = false;
				$(".error-panel").text(i18n("connection.validation.prompt"));
			}
		});
		return valid;
	};
	humanReadableName = function() {
		return fconnection[fconnection.getType()].humanReadableName;
	};
	toggleSubFields = function(key) {
		if(isSubsection(key)) {
			// TODO why can't these be combined?
			if(!getFieldVal(key)) {
				disableSubsectionFields(key);
			} else {
				enableSubsectionFields(key);
			}
		}	
	};

	handleSaveResponse = function(response) {
		if(response.ok) {
			window.location = response.redirectUrl;
		} else {
			var errors = $(".error-panel");
			errors.text(response.text);
			errors.show();
			$("#submit").removeAttr('disabled');
		}
	};

	return {
		attachCheckBoxListener:attachCheckBoxListener,
		handleSaveResponse:handleSaveResponse,
		init:init,
		isValid:isValid,
		validator:validator,
		show:show
	};
}());

