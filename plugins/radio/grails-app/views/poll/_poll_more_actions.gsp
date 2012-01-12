<g:select class="dropdown more-actions poll-btn" name="more-actions" from="${['Export', 'Rename ' + messageSection, 'Delete ' + messageSection, 'Add to Show']}"
			keys="${['export', 'rename', 'delete', 'radioShow']}"
			noSelection="${['': 'More actions...']}"/>
<g:javascript>
function radioShowAction() {
	$.ajax({
		type:'GET',
		url: url_root + 'radioShow/selectPoll',
		data: {ownerId: $("#ownerId").val()},
		success: function(data) {
			launchSmallPopup('Add to Show', data, 'Add');
	}})
}
</g:javascript>