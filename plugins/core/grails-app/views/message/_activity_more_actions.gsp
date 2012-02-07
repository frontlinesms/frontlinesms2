<g:select class="dropdown more-actions poll-btn" name="more-actions"
		from="${['Export', 'Rename ' + ownerInstance?.type, 'Delete ' + ownerInstance?.type]}"
			keys="${['export', 'rename', 'delete']}"
			noSelection="${['': 'More actions...']}"/>