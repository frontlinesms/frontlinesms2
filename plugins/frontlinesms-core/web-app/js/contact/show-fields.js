$(function() {
	initContactPaneFields();
});

function initContactPaneFields() {
	$("a.remove-command.custom-field").click(removeFieldClickAction);
	$("#new-field-dropdown").change(addFieldClickAction);
	$("a.remove-command.not-custom-field").click(clearField);
}

function addFieldClickAction() {
	var me, fieldName, fieldValue;
	me = $(this).find('option:selected');
	if(me.hasClass('not-field')) { return; }
	if(me.hasClass('create-custom-field')) {
		$.ajax({
			type:'POST',
			url: url_root + 'contact/newCustomField',
			beforeSend : function() { showThinking(); },
			success: function(data, textStatus) {
				hideThinking();
				launchSmallPopup(i18n("smallpopup.customfield.create.title"), data, i18n("action.ok"), clickDone);
			}
		});
	} else {
		fieldName = me.text();
		fieldValue = "";
		addCustomField(fieldName);
		me.remove();
	}
	selectmenuTools.snapback(this);
	enableSaveAndCancel();
}

function clickDone() {
	if ($("#modalBox").contentWidget("onDone")) {
		$(this).find("form").submit();
	}
}

var customFields = (function() {
	var
	checkResults = function(json) {
		var name, fieldsToAdd, x, y;
		if ($("#custom-field-name").val() !== "") {
			name = $("#custom-field-name").val();
			fieldsToAdd = getFieldIdList('fieldsToAdd').val().split(",");
			for (y in fieldsToAdd) {
				if(fieldsToAdd[y] !== "") { json.uniqueCustomFields.push(fieldsToAdd[y]); }
			}
			for (x in json.uniqueCustomFields) {
				if (json.uniqueCustomFields[x].toLowerCase() === name.toLowerCase()) {
					$("#smallpopup-error-panel").html(i18n("customfield.validation.error"));
					$("#smallpopup-error-panel").show();
					return false;
				}
			}
			addCustomField(name);
			$("#modalBox").remove();
		} else {
			$("#smallpopup-error-panel").html(i18n("customfield.validation.prompt"));
			$("#smallpopup-error-panel").show();
		}
	};
	return {
		checkResults:checkResults
	};
}());

function addCustomField(name) {
	var fieldId, fieldRow, textFieldItem, deleteButton, fieldTd;
	fieldId = Math.floor(Math.random() * 100001);
	fieldRow = $('<tr><td><label for="' + fieldId + '">' + name + '</label></td></tr>');
	textFieldItem = $('<input type="text" name="' + name + '"/>');
	deleteButton = $('<a class="remove-command unsaved-field custom-field" id="remove-field-' + fieldId + '">&nbsp;</a>');

	fieldTd = $("<td/>");
	fieldTd.append(textFieldItem);
	fieldTd.append(deleteButton);
	fieldRow.append(fieldTd);

	deleteButton.click(removeFieldClickAction);

	$('#info-add').parent().before(fieldRow);
	$('input[name="' + name + '"]').focus();
	addField(name);
}

function removeFieldClickAction() {
	var fieldId, fieldElement, isUnsaved, fieldName;
	fieldId = $(this).attr('id').substring('remove-field-'.length);
	fieldElement = $(this).parent().parent();
	isUnsaved = $(this).hasClass('unsaved-field');
	fieldName = fieldElement.find('input').attr('name');
	fieldElement.remove();
	$("#new-field-dropdown option[value='na']").after('<option value="'+fieldName+'">'+fieldName+'</option>');
	selectmenuTools.refresh($('#new-field-dropdown'));
	removeFieldId(fieldId, fieldName, isUnsaved);
	enableSaveAndCancel();
}

function removeFieldId(id, name, isUnsaved) {
	if(isUnsaved) {
		// remove from the ADD list
		removeFieldIdFromList(name, 'fieldsToAdd');
	} else {
		// add to the REMOVE list
		addFieldIdToList(id, 'fieldsToRemove');
	}
}
function addField(id) {
	// remove from the REMOVE list
	removeFieldIdFromList(id, 'fieldsToRemove');
	// add to the ADD list
	addFieldIdToList(id, 'fieldsToAdd');
}

function removeFieldIdFromList(id, fieldName) {
	var f, oldList, newList;
	f = $('input:hidden[name=' + fieldName + ']');
	oldList = f.val();
	newList = oldList.replace(','+ id +',', ',');
	f.val(newList);
}
function addFieldIdToList(id, fieldName) {
	var f, oldList, newList;
	f = getFieldIdList(fieldName);
	oldList = f.val();
	newList = oldList + id + ',';
	f.val(newList);
}
function getFieldIdList(fieldName) {
	return $('input:hidden[name=' + fieldName + ']');
}
function clearField() {
	var field;
	if($(this).attr('id')) {
		field = $(this).attr('id').substring('remove-'.length);
		$('#' + field).val('');
		$(this).hide();
		$(this).next().hide();
	}		
}
