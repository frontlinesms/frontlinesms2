$(document).ready(function() {
	$("#custom-field-list li a.remove-field").click(removeFieldClickAction);
	$("#new-field-dropdown").change(addFieldClickAction);
});

function customFieldPopup(data) {
		$(data).dialog({title: "Create Custom Field", width: 600});
}

function addFieldClickAction() {
	var me = $(this).find('option:selected');
	if(me.hasClass('not-field')) return;
	if(me.hasClass('create-custom-field')) {
		$.ajax({
			type:'POST',
			url: '/frontlinesms2/contact/newCustomField',
			success: function(data, textStatus){ customFieldPopup(data); }
		});
		$("#new-field-dropdown").val("na");
		return false;
	}
	$("#new-field-dropdown").val("na");

	var fieldName = me.text();
	var fieldValue = "";
	addCustomField(fieldName, fieldValue);
}

function addCustomField(name, value) {
	var fieldId = Math.floor(Math.random()*100001)
	var fieldListItem = $('<li><label for="' + fieldId + '">' + name + '</label>');
	var textFieldItem = $('<input type="text" name="' + name + '" value="' + value + '" />');
	var deleteButton = $('<a class="remove-field" id="remove-field-' + fieldId + '">Delete</a></li>');

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

function createCustomField_submit() {
	var name = $('#custom-field-name').val();
	var value = $('#custom-field-value').val();
	if(!name.length || !value.length) {
		if(!$('#invalid').length) {
			$('#custom-field-popup').prepend("<p id='invalid'>invalid details</p>");
		}
	} else {
		addCustomField(name, value);
		$("#custom-field-popup").dialog('close');
	}
	return false;
}

function createCustomField_cancel() {
	$("#custom-field-popup").dialog('close');
	return false;
}

var createCustomField = {
	submit: function() {
		  var name = $('#custom-field-name').val();
		  var value = $('#custom-field-value').val();
		  if(!name.length || !value.length) {
			  if(!$('#invalid').length) {
				  $('#custom-field-popup').prepend("<p id='invalid'>invalid details</p>");
			  }
			  return false;
		  }
		  alert("We think we checked");
		  addCustomField(name, value);

		  alert("We think we added a new field");
		  $("#custom-field-popup").dialog('close');
		  return false;
	},

	cancel: function() {
		$("#custom-field-popup").dialog('close');
		return false;
	}
}