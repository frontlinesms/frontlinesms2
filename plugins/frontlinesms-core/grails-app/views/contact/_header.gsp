<div class="content ${contactsSection?.shortName?:'all'}">
	<g:if test="${contactsSection instanceof frontlinesms2.Group || contactsSection instanceof frontlinesms2.SmartGroup}">
		<h1>${contactsSection.name} (${contactsSectionContactTotal})</h1>
		<div class="header-buttons">
			<g:if test="${contactsSection instanceof frontlinesms2.Group}">
				<g:select class="dropdown" name="group-actions" from="${[message(code: 'group.rename'), message(code: 'group.delete')]}"
							keys="${['rename', 'delete']}"
							noSelection="${['': g.message(code:'group.moreactions')]}"
							onchange="selectmenuTools.snapback(this)"/>
			</g:if>
			<g:else>
				<g:select class="dropdown" name="group-actions" from="${[message(code: 'group.rename'), message(code: 'group.edit'), message(code: 'group.delete')]}"
							keys="${['rename', 'edit', 'delete']}"
							noSelection="${['': g.message(code:'group.moreactions')]}"
							onchange="selectmenuTools.snapback(this)"/>
			</g:else>
		</div>
	</g:if>
	<g:else>
		<h1><g:message code="contact.all.contacts" args="${[contactInstanceTotal]}"/></h1>
	</g:else>
</div>

