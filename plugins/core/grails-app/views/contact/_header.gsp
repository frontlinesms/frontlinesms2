<div class="section-header" id="contact-header">
	<div id="contact-title">
		<g:if test="${contactsSection instanceof frontlinesms2.Group || contactsSection instanceof frontlinesms2.SmartGroup}">
			<h3 id="${contactsSection instanceof frontlinesms2.Group ? 'group' : 'smart-group'}-title">${contactsSection.name} (${contactInstanceTotal})</h3>
			<ul class="section-header-buttons button-list">
				<g:if test="${contactsSection instanceof frontlinesms2.Group}">
					<li>
						<g:select class="dropdown" name="group-actions" from="${['Rename group', 'Delete group']}"
								keys="${['rename', 'delete']}"
								noSelection="${['': 'More actions...']}" />
					</li>
				</g:if>
				<g:else>
					<li>
						<g:select class="dropdown" name="group-actions" from="${['Rename group', 'Edit group', 'Delete group']}"
								keys="${['rename', 'edit', 'delete']}"
								noSelection="${['': 'More actions...']}" />
					</li>
				</g:else>
				<li>
					<g:remoteLink class="btn" controller="export" action="contactWizard" params="[groupId: contactsSection?.id, contactsSection:contactsSection instanceof frontlinesms2.Group ? 'group' : 'smartGroup']" onSuccess="launchSmallPopup('Export', data, 'Export')"><g:message code="contact.export" /></g:remoteLink>
				</li>
			</ul>
		</g:if>
		<g:else>
			<h3 id="all-contacts-title">${contactInstance?.name ?: contactInstance?.mobile ?: 'New Contact'}</h3>
			<ul class="section-header-buttons button-list">
				<li>
					<g:remoteLink class="btn" controller="export" action="contactWizard" onSuccess="launchSmallPopup('Export', data, 'Export')"><g:message code="contact.export" /></g:remoteLink>
				</li>
			</ul>
		</g:else>
	</div>
</div>
