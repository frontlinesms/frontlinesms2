<g:if test="${ownerInstance && !(ownerInstance instanceof frontlinesms2.Announcement)}">

	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
		from="${[g.message(code:'message.activity.actions.export'), g.message(code:'message.activity.actions.rename', args:[ownerInstance?.type]), g.message(code:'message.activity.actions.edit', args:[ownerInstance?.type]), g.message(code:'message.activity.actions.delete', args:[ownerInstance?.type])]}"
			keys="${['export', 'rename', 'edit', 'delete']}"
			noSelection="${['': g.message(code:'message.activity.actions.moreactions')]}"/>
</g:if>
<g:else>
	<g:select class="dropdown more-actions activity-btn" name="more-actions"
		from="${[g.message(code:'message.activity.actions.export'), g.message(code:'message.activity.actions.rename', args:[ownerInstance?.type]), g.message(code:'message.activity.actions.delete', args:[ownerInstance?.type])]}"]}"
			keys="${['export', 'rename', 'delete']}"
			noSelection="${['': g.message(code:'message.activity.actions.moreactions')]}"/>
</g:else>

