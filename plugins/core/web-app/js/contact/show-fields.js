$(function() {
	$("a.remove-command.custom-field").click(removeFieldClickAction);
	$("#new-field-dropdown").change(addFieldClickAction);
	$("a.remove-command.not-custom-field").click(clearField);
});

function addFieldClickAction() {
	var me = $(this).find('option:selected');
	$("#new-field-dropdown").val("na");
	if(me.hasClass('not-field')) return;
	if(me.hasClass('create-custom-field')) {
		$.ajax({
			type:'POST',
			url: url_root + 'contact/newCustomField',
			success: function(data, textStatus) { launchSmallPopup(i18n("smallpopup.customfield.create.title"), data, i18n('smallpopup.ok'), clickDone); }
		});
	} else {
		var fieldName = me.text();
		var fieldValue = "";
		addCustomField(fieldName);
		me.remove();
	}
	selectmenuTools.refresh($('#new-field-dropdown'));
	enableSaveAndCancel();
}

function clickDone() {
	if ($("#custom-field-name").val() != "") {
		var name = $("#custom-field-name").val();
		addCustomField(name);
		$(this).remove();
	} else {
		$("#custom-field-popup .error-panel").removeClass("hide");
	}
}

function addCustomField(name) {
	var fieldId = Math.floor(Math.random()*100001)
	var fieldListItem = $('<tr><td><label class="why" for="' + fieldId + '">' + name + '</label></td></tr>');
	var textFieldItem = $('<td><input type="text" name="' + name + '" value="" /></td>');
	var deleteButton = $('<a class="remove-command unsaved-field custom-field" id="remove-field-' + fieldId + '"></a>');
	var deleteButtonTd = $('<td></td>');

	deleteButtonTd.append(deleteButton);
	fieldListItem.append(textFieldItem);
	fieldListItem.append(deleteButtonTd);
	deleteButton.click(removeFieldClickAction);

	$('#info-add').parent().before(fieldListItem);
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
	if(isUnsaved)
		// remove from the ADD list
		removeFieldIdFromList(name, 'fieldsToAdd');
	else
		// add to the REMOVE list
		addFieldIdToList(id, 'fieldsToRemove');
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
	var f = $('input:hidden[name=' + fieldName + ']');
	var oldList = f.val();
	var newList = oldList + id + ',';
	f.val(newList);
}

function clearField() {
	if($(this).attr('id')) {
		var field = $(this).attr('id').substring('remove-'.length);
		$('#' + field).val('');
		$(this).hide()
		$(this).next().hide()
	}		
}
