<g:if test="${ownerInstance && ownerInstance instanceof frontlinesms2.Poll}">
	<g:select class="dropdown more-actions poll-btn" name="more-actions"
		from="${['Export', 'Rename ' + ownerInstance?.type, 'Edit ' + ownerInstance?.type, 'Delete ' + ownerInstance?.type, 'Add to Show']}"
			keys="${['export', 'rename', 'edit', 'delete', 'radioShow']}"
			noSelection="${['': 'More actions...']}"/>
</g:if>
<g:else>
	<g:select class="dropdown more-actions poll-btn" name="more-actions"
		from="${['Export', 'Rename ' + ownerInstance?.type, 'Delete ' + ownerInstance?.type, 'Add to Show']}"
			keys="${['export', 'rename', 'delete', 'radioShow']}"
			noSelection="${['': 'More actions...']}"/>
</g:else>
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
