function getGroupId(){
	var group = $('#groupId');
	return group.length ? group.val() : '';
}

function updateContacts(data) {
	var replacement = $(data);
	$("#contact-list").replaceWith(replacement.filter('#contact-list'));
	$("#paging").replaceWith(replacement.find('#paging'));
	disablePaginationControls();
}

$(function() {  
   disablePaginationControls();
   $("#contact-search").renderDefaultText();
});