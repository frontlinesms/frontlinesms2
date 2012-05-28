function getGroupId(){
	var group = $('#groupId');
	return group.length ? group.val() : '';
}

function updateContacts(data) {
	var replacement = $(data);
	$("#contact-list").html(replacement.filter('#contact-list').html());
	$(".footer #paging").html(replacement.filter('.footer #paging').html());
	if($("#contact-search").val()=="") {
		$("#paging").append('<a href="#" class="nextLink enabled"></a>');
	}
	disablePaginationControls();
}

$(function() {  
   disablePaginationControls();
   $("#contact-search").renderDefaultText();
});
