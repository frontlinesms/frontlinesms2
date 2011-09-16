function contactChecked(contactId) {
	var count = countCheckedContacts();
	if($('#contact-list #contact-' + contactId).find('input[type=checkbox]').attr('checked')) {
		addToChecked(contactId);
		if(count == 1) {
			$('#contact-list').find('.selected').removeClass('selected');
			loadSingleContact(contactId);
		} else {
			loadMultipleContacts(count);
		}
		$("#contact-" + contactId).addClass('selected');
	} else {
		if(count != 0) {
			removeFromChecked(contactId);
			$("#contact-" + contactId).removeClass('selected');
			if (count == 1) {
				var newContactRowId = $('#contact-list').find('.selected').attr('id');
				var newContactId = newContactRowId.substring('contact-'.length);
				loadSingleContact(newContactId);
			} else {
				loadMultipleContacts(count);
			}
		} else {
			$('input:hidden[name=checkedContactList]').val(',');
		}
	}
}

function countCheckedContacts(){
    return $('input[name=contact-select]:checked').size();
}

function loadSingleContact(contactId) {
	$('input:hidden[name=checkedContactList]').val(',' + contactId + ',');
	$.get(url_root + 'contact/show', { 'contactId': contactId }, function(data) {
		if($('div.single-contact').is(':hidden')) {
			$('.multiple-contact').hide();
			$('.single-contact').show();
		}
		$('#contact-title').replaceWith($(data).find('#contact-title'));
		$('.single-contact').replaceWith($(data).find('.single-contact'));
		$("#group-list li a.remove-group").click(removeGroupClickAction);
		$("#group-dropdown").change(addGroupClickAction);
	});
}

function loadMultipleContacts(count) {
	$('#contact-count').html("<p> " + count + " contacts selected</p>");
	
	if($('div.multiple-contact').is(':hidden')) {
		$('.multiple-contact').show();
		$('.single-contact').hide();
	}

	// The following call is asynchronous, so we need to perform it after others have completed.
	var contactIds = $('input:hidden[name=checkedContactList]').val();
	$.get(url_root + 'contact/multipleContactGroupList', { 'checkedContactList': contactIds }, function(data) {
		$('#multi-group-list').replaceWith($(data).find('#multi-group-list'));
		$('#multi-group-add').replaceWith($(data).find('#multi-group-add'));
		$("#multi-group-dropdown").change(addGroupClickAction);
		$("#multi-group-list li a.remove-group").click(removeGroupClickAction);
	});
}

function addToChecked(contactId) {
	var contactList = $('input:hidden[name=checkedContactList]');
	var oldList = contactList.val();
	var newList = oldList + contactId + ',';
	contactList.val(newList);
}

function removeFromChecked(contactId) {
	var contactList = $('input:hidden[name=checkedContactList]');
	var newList = jQuery.grep(contactList.val().split(","), function(element, index) {return element != contactId}).join(",");
	contactList.val(newList);
}
