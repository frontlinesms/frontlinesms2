function updateContactDetails(id){
	highlightRow(id);
	removeSelectedContact();
	var count = countCheckedContacts();
	
	//update checked contact list
	$('input:hidden[name=contactIds]').val(getCheckedContacts());
	
	if(count == 1) {
		loadContact($(':checkbox[checked="true"]').val());
	}
	if(count > 1){
		changeContactCount(count);
		loadGroupList();
	}
}

function removeSelectedContact() {
	if($('li.selected :checkbox[checked="false"]').is(':visible'))
		$('#contact-' + $('li.selected :checkbox[checked="false"]').val()).removeClass('selected');
}

function loadContact(id) {
	$.get(url_root + 'contact/show', { 'contactId': id, 'checkedId': id}, function(data) {
		if($('div.single-contact').is(':hidden')) {
			showContactDetails();
		}
		$('#contact_details').replaceWith($(data).find('#contact_details'));
		$("#group-list li a.remove-group").click(removeGroupClickAction);
		$("#group-dropdown").change(addGroupClickAction);
	});
}

function enableSingleAction() {
	$('.multiple-contact').hide()
	$('.single-contact').show()
}

function showContactDetails(){
	enableSingleAction();
}

function countCheckedContacts(){
     return getSelectedContacts().size();
}

function changeContactCount(count){
	$('#count').html("<p> "+count+" contacts selected</p>");
	setContactDetails();
}


function setContactDetails() {
	$('.multiple-contact').show();
	$('.single-contact').hide();
}

function highlightRow(id){
	if($(':checkbox[value=' + id +']').attr('checked','true')){
		$("#contact-" + id).addClass('selected');
	} else {
		$("#contact-" + id).removeClass('selected');	
	}
}

function getSelectedContacts() {
	return $('input[name=contact]:checked');
}

function getCheckedContacts() {
	var idsToDelete = []
	$.each(getSelectedContacts(), function(index, value) {
		if(isValid(value.value)) {
			idsToDelete.push(value.value)
		}
	});
	return idsToDelete;
}

function isValid(value) {
		return value && value != "0"
}

function getSelectedContacts() {
	return $('input[name=contact]:checked');
}

function validateDelete() {
	return true;
}

function loadGroupList() {
	var contactIds = $('input:hidden[name=contactIds]').val();
	$.get(url_root + 'contact/multipleContactGroupList', { 'contactIds': contactIds}, function(data) {
		$('#multi-group-list').replaceWith($(data).find('#multi-group-list'));
		$('#multi-group-add').replaceWith($(data).find('#multi-group-add'));
		$("#multi-group-dropdown").change(addGroupClickAction);
		$("#multi-group-list li a.remove-group").click(removeGroupClickAction);
	});
}
