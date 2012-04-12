<g:if test="${ownerInstance && !(ownerInstance instanceof frontlinesms2.Announcement)}">

	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
		from="${[g.message(code:'fmessage.export'), g.message(code:'fmessage.rename', args:[ownerInstance?.type]), g.message(code:'fmessage.edit', args:[ownerInstance?.type]), g.message(code:'fmessage.delete', args:[ownerInstance?.type])]}"
			keys="${['export', 'rename', 'edit', 'delete']}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"/>
</g:if>
<g:else>
	<g:select class="dropdown more-actions activity-btn" name="more-actions"
		from="${[g.message(code:'fmessage.export'), g.message(code:'fmessage.rename', args:[ownerInstance?.type]), g.message(code:'fmessage.delete', args:[ownerInstance?.type])]}"]}"
			keys="${['export', 'rename', 'delete']}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"/>
</g:else>

