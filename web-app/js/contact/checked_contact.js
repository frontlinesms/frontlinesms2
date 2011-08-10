var selectedContactId;

function updateContactDetails(id){
	highlightRow(id);

	var count = countCheckedContacts();
	if(count == 1) {
		if(selectedContactId != $('li :checkbox[checked="true"]').val())
			loadContact($('li :checkbox[checked="true"]').val(), true);
	}
	if(count > 1){
		changeContactCount(count);
	}
}

function loadContact(id, checked) {
	var url = $(".displayName-" + id).attr("href")
	if(checked == true){
	   url = url + (url.indexOf("?") > -1 ? "&" : "?")
		url = url + "checkedId="+id;
	}
	window.location = url
}

function countCheckedContacts(){
     return getSelectedContacts().size();
}

function changeContactCount(count){
	setSelectedContact();
	$('#count').html("<p> "+count+" contacts selected</p>");
	setContactDetails();
}

function setSelectedContact() {
	if(selectedContactId == null){
		selectedContactId = $('li.selected').attr('id').substring('contact-'.length);
	}
}

function setContactDetails() {
	$('.multi-action').show();
	$('.single-action').hide();
}

function highlightRow(id){
	if( $('li :checkbox[value='+id+']').attr('checked') == 'checked'){
		$("#contact-"+id).addClass('checked');
	} else {
		$("#contact-"+id).removeClass('checked');
	}
}

function getSelectedContacts() {
	return $('input[name=contact]:checked');
}

$('#btn_delete_all').live('click', function() {
	var idsToDelete = []
	$.each(getSelectedContacts(), function(index, value) {
		if(isValid(value.value)) {
			idsToDelete.push(value.value)
		}
	});
	if(confirm("Delete " + getSelectedContacts().size() + " contacts")) {
		$.ajax({
			type:'POST',
			traditional: true,
			data: {ids: idsToDelete},
			url: '/frontlinesms2/contact/deleteContact',
			success: function(data) { location.reload()}
		});
	}
});

function isValid(value) {
		return value && value != "0"
}
