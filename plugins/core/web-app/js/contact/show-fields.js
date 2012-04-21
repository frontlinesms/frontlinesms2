$(document).ready(function() {
	$("#custom-field-list li a.remove-field").click(removeFieldClickAction);
	$("#new-field-dropdown").change(addFieldClickAction);
	$("div.basic-info a.remove-field").click(clearField);
});

function addFieldClickAction() {
	var me = $(this).find('option:selected');
	$("#new-field-dropdown").val("na");
	if(me.hasClass('not-field')) return;
	if(me.hasClass('create-custom-field')) {
		$.ajax({
			type:'POST',
			url: url_root + 'contact/newCustomField',
			success: function(data, textStatus) { launchSmallPopup(i18n("smallpopup.customfield.create.title"), data, "Ok", clickDone); }
		});
	} else {
		var fieldName = me.text();
		var fieldValue = "";
		addCustomField(fieldName);
	}
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
	var fieldListItem = $('<li><label for="' + fieldId + '">' + name + '</label>');
	var textFieldItem = $('<input type="text" name="' + name + '" value="" />');
	var deleteButton = $('<a class="remove-field" id="remove-field-' + fieldId + '"></a></li>');
	
	fieldListItem.append(textFieldItem);
	fieldListItem.append(deleteButton);
	deleteButton.click(removeFieldClickAction);

	$('#custom-field-list').append(fieldListItem);
	$('input[name="' + name + '"]').focus();
	addField(name);
}

function removeFieldClickAction() {
	var fieldId = $(this).attr('id').substring('remove-field-'.length);
	var parent = $(this).parent();
	parent.remove();

	removeFieldId(fieldId);
}

function removeFieldId(id) {
	// remove from the ADD list
	removeFieldIdFromList(id, 'fieldsToAdd');
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
