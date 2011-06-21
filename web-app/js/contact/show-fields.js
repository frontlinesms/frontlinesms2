$(document).ready(function() {
	$("#custom-field-list li a.remove-field").click(removeFieldClickAction);
	$("#new-field-dropdown").change(addFieldClickAction);
});

function addFieldClickAction() {
	var me = $(this).find('option:selected');
	if(me.hasClass('not-field')) return;
	if(me.hasClass('create-custom-field')) {
		window.location = "/frontlinesms2/contact/newCustomField";
		return;
	}
	$("#new-field-dropdown").val("na");

	var fieldId = me.attr('id');
	var fieldName = me.text();

	var fieldListItem = $('<li><label for="custom-field">' + fieldName + '</label>');
	var textFieldItem = $('<input type="text" name="' + fieldName + '" value="" />');
	var deleteButton = $('<a class="delete-field" id="delete-field-' + fieldId + '">Delete</a></li>');

	deleteButton.click(removeFieldClickAction);
	fieldListItem.append(textFieldItem);
	fieldListItem.append(deleteButton);

	$('#custom-field-list').append(fieldListItem);
	$('input[name="' + fieldName + '"]').focus();
	addField(fieldName);
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