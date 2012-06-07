function getGroupId(){
	var group = $('#groupId');
	return group.length ? group.val() : '';
}

function updateContacts(data) {
	var replacement = $(data);
	$("#main-list").replaceWith(replacement.filter('#main-list'));
	$("#paging").replaceWith(replacement.find('#paging'));
	disablePaginationControls();
}

$(function() {  
   disablePaginationControls();
   $("#contact-search").renderDefaultText();
});