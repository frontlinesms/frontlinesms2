<g:if test="${ownerInstance && !(ownerInstance instanceof frontlinesms2.Announcement)}">
	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
			from="${['export', 'rename', 'edit', 'delete']}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"
			valueMessagePrefix="${ownerInstance.shortName}.moreactions"/>
</g:if>
<g:else>
	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
			from="${['export', 'rename', 'delete']}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"
			valueMessagePrefix="${ownerInstance.shortName}.moreactions"/>
</g:else>
