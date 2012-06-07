<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.Fconnection" %>
<meta name="layout" content="popup"/>
<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="connection.validation.prompt" /></div>
	<ol>
		<g:if test="${!fconnectionInstance}">
			<li><a href="#tabs-1"><g:message code="connection.type" /></a></li>
		</g:if>
		<li><a href="#tabs-2"><g:message code="connection.details" /></a></li>
		<li><a href="#tabs-3"><g:message code="connection.confirm" /></a></li>
	</ol>
	<g:formRemote name="connectionForm" url="[controller:'connection', action:action, id:fconnectionInstance?.id, params:[format:'json', connectionType:fconnectionInstance?.shortName]]" method="post" onLoading="showThinking()" onSuccess="hideThinking(); handleSaveResponse(data)">
		<fsms:wizardTabs templates="type, details, confirm"/>
	</g:formRemote>
</div>

<r:script>

var fconnection = {
	getType: function() {
		<g:if test="${fconnectionInstance}">return "${fconnectionInstance?.shortName}";</g:if>
		<g:else>return $("input[name=connectionType]").val();</g:else>
	},
	setType: function(connectionType) {
		$("input[name=connectionType]").val(connectionType);
		<g:each in="${Fconnection.implementations*.shortName}">
			$("#${it}-form").hide();
		</g:each>
		$("#" + connectionType + "-form").css('display', 'inline');
		fconnection.init();
	},
	init: function() {
		var keys = fconnection[fconnection.getType()].configFieldsKeys;
		$.each(keys, function(index, value) {
			fconnection.toggleSubFields(value);
		});
	},
	show: function() {
		setConfirmVal('type', fconnection.humanReadableName());
		setConfirmation('name');
		fconnection[fconnection.getType()].show();
	},
	isValid: function() {
		var valid = false;
		var keys = fconnection[fconnection.getType()].configFieldsKeys
		if(keys.length > 1) {
			valid = validateSections(keys)
			if(!valid) return valid;
			$.each(keys, function(index, value) {
				valid = isFieldValid(value);
				return valid;
			});
		} else {
			var fields = fconnection[fconnection.getType()].requiredFields
			$.each(fields, function(index, value) {
				valid = isFieldValid(value);
				return valid;
			});
		}
		return valid;
	},
	humanReadableName: function() {
		return fconnection[fconnection.getType()].humanReadableName;
	},
	toggleSubFields: function(key) {
		if(isSubsection(key)) {
			if(!getFieldVal(key))
				disableSubsectionFields(key);
			if(getFieldVal(key))
				enableSubsectionFields(key);
				
		}	
	},

	<g:each in="${Fconnection.implementations}" var="imp">
	${imp.shortName}: {
		requiredFields: ["${Fconnection.getNonnullableConfigFields(imp).join('", "')}"],
		configFieldsKeys: ["${imp.configFields instanceof Map ? imp.configFields.getAllKeys()?.join('", "'):''}"],
		humanReadableName: "<g:message code="${imp.simpleName.toLowerCase()}.label"/>",
		show: function() {
			<g:each in="${(Fconnection.implementations - imp)*.shortName}">
				$("#${it}-confirm").hide();
			</g:each>
			var configFieldsKeys = fconnection[fconnection.getType()].configFieldsKeys
			if(configFieldsKeys.length > 1) {
				$.each(configFieldsKeys, function(index, value) {
					setConfirmation(value);
				});
			}
			<g:set var="configFields" value="${imp.configFields instanceof Map? (imp.configFields.getAllValues()) : imp.configFields}" />
			<g:each in="${configFields}" var="f">
				<g:if test="${f in imp.passwords}">setSecretConfirmation('${f}');</g:if>
				<g:else>setConfirmation('${f}');</g:else>
			</g:each>
			$("#${imp.shortName}-confirm").show();
		}
	},
	</g:each>
};
			
function isFieldSet(fieldName) {
	var val = getFieldVal(fieldName);
	if(isInstanceOf(val, 'Boolean')) {
		if(val && isSubsection(fieldName)) {
			return validateSubsectionFields(fieldName);
		}
	} else {
		return val!=null && val.length>0;
	}
}

function isFieldValid(field) {
	return isFieldSet(field);
}

function isInstanceOf(obj, clazz){
  return (obj instanceof eval("("+clazz+")")) || (typeof obj == clazz.toLowerCase());
}

function validateSubsectionFields(field) {
	var valid = false;
	var subSectionFields = $('.' + field + '-subsection-member');
	var requiredFields = fconnection[fconnection.getType()].requiredFields
	$.each(subSectionFields, function(index, value) {
		var field = $(value).attr("field");
		if(requiredFields.indexOf(field) > -1) {
			valid = isFieldSet(field);
			return valid;
		}
	});
	return valid;
}

function validateSections(keys) {
	var valid = false;
	$.each(keys, function(index, value) {
		if(isSubsection(value)) {
			valid = getFieldVal(value);
			if(valid) return false;
		}
	});
	return valid;
}

function isSubsection(fieldName) {
	return $('#' + fieldName + '-subsection').length > 0;
}

function disableSubsectionFields(field) {
	var subSectionFields = $('.' + field + '-subsection-member');
	$.each(subSectionFields, function(index, value){
		$(value).disableField();
	});
}

function enableSubsectionFields(field) {
	var subSectionFields = $('.' + field + '-subsection-member');
	$.each(subSectionFields, function(index, value){
		$(value).enableField();
	});
}

function getFieldVal(fieldName) {
	if($('#' + fconnection.getType() + fieldName).attr("type") == "checkbox") {
		var val = $('#' + fconnection.getType() + fieldName).prop("checked");
	} else {
		var val = $('#' + fconnection.getType() + fieldName).val();
	}
	return val;
}

function setConfirmVal(fieldName, val) {
	var isCheckbox = $('#' + fconnection.getType() + fieldName).attr("type") == "checkbox";
	
	if(isCheckbox == true) {
		var text = (val == true) ? "Yes": "No";
		$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).text(text);
	} else if($('#' + fconnection.getType() + fieldName).is(":disabled") === false) {
		$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).parent().removeClass("hide");
		$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).text(val);
	} else {
		$("#" + fconnection.getType() + "-confirm #confirm-" + fieldName).parent().addClass("hide");
	}
}

function setConfirmation(fieldName) {
	setConfirmVal(fieldName, getFieldVal(fieldName));
}

function setSecretConfirmation(fieldName) {
	val = isFieldSet(fieldName)? '****': 'None';
	setConfirmVal(fieldName, val);
}

function attachCheckBoxListener() {
	$("input[type='checkbox']").bind("change", function(){
		var key = $(this).attr("field");
		fconnection.toggleSubFields(key);
	});
}

function initializePopup() {
	$("#connectionForm").validate();
	<g:if test="${!fconnectionInstance}">
		fconnection.setType("${fconnectionInstance?fconnectionInstance.getClass().shortName: 'smslib'}");
	</g:if>
	
	fconnection.init();
	$("#tabs").bind("tabsshow", fconnection.show);
	attachCheckBoxListener();
	$("#tabs-2").contentWidget({
		validate: fconnection.isValid
	});
}

function handleSaveResponse(response) {
	if(response.ok) {
		window.location = response.redirectUrl;
	} else {
		var errors = $(".error-panel");
		errors.text(response.text);
		errors.show();
		$("#submit").removeAttr('disabled');
	}
}
</r:script>
