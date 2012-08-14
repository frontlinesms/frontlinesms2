$(function() {
	initContactPaneFields();
});

function initContactPaneFields() {
	$("a.remove-command.custom-field").click(removeFieldClickAction);
	$("#new-field-dropdown").change(addFieldClickAction);
	$("a.remove-command.not-custom-field").click(clearField);
}

function addFieldClickAction() {
	var me = $(this).find('option:selected');
	if(me.hasClass('not-field')) return;
	if(me.hasClass('create-custom-field')) {
		$.ajax({
			type:'POST',
			url: url_root + 'contact/newCustomField',
			success: function(data, textStatus) { launchSmallPopup(i18n("smallpopup.customfield.create.title"), data, i18n("action.ok"), clickDone); }
		});
	} else {
		var fieldName = me.text();
		var fieldValue = "";
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

var CustomFields = function() {
	var
		_checkResults = function(json) {
			if ($("#custom-field-name").val() != "") {
				var name = $("#custom-field-name").val();
				var fieldsToAdd = getFieldIdList('fieldsToAdd').val().split(",");
				for (y in fieldsToAdd) {
					if(fieldsToAdd[y] !="" ) {json.uniqueCustomFields.push(fieldsToAdd[y])};
				}
				for (x in json.uniqueCustomFields) {
					if (json.uniqueCustomFields[x].toLowerCase() == name.toLowerCase()) {
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
		checkResults: _checkResults
	};
};
var customFields = new CustomFields();

function addCustomField(name) {
	var fieldId = Math.floor(Math.random() * 100001);
	var fieldRow = $('<tr><td><label for="' + fieldId + '">' + name + '</label></td></tr>');
	var textFieldItem = $('<input type="text" name="' + name + '"/>');
	var deleteButton = $('<a class="remove-command unsaved-field custom-field" id="remove-field-' + fieldId + '">&nbsp;</a>');

	var fieldTd = $("<td/>");
	fieldTd.append(textFieldItem);
	fieldTd.append(deleteButton);
	fieldRow.append(fieldTd);

	deleteButton.click(removeFieldClickAction);

	$('#info-add').parent().before(fieldRow);
	$('input[name="' + name + '"]').focus();
	addField(name);
}

function removeFieldClickAction() {
	var fieldId = $(this).attr('id').substring('remove-field-'.length);
	var fieldElement = $(this).parent().parent();
	var isUnsaved = $(this).hasClass('unsaved-field');
	var fieldName = fieldElement.find('input').attr('name');
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
	var f = $('input:hidden[name=' + fieldName + ']');
	var oldList = f.val();
	var newList = oldList.replace(','+ id +',', ',');
	f.val(newList);
}
function addFieldIdToList(id, fieldName) {
	var f = getFieldIdList(fieldName);
	var oldList = f.val();
	var newList = oldList + id + ',';
	f.val(newList);
}
function getFieldIdList(fieldName) {
	return $('input:hidden[name=' + fieldName + ']');
}
function clearField() {
	if($(this).attr('id')) {
		var field = $(this).attr('id').substring('remove-'.length);
		$('#' + field).val('');
		$(this).hide();
		$(this).next().hide();
	}		
}
