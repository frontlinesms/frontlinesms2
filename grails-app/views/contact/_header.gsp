<div class="section-header" id="contact-header">
	<div id="contact-title">
		<g:if test="${contactsSection instanceof frontlinesms2.Group || contactsSection instanceof frontlinesms2.SmartGroup}">
			<h3>${contactsSection.name} (${contactInstanceTotal})</h3>
			<ul class="section-header-buttons button-list">
				<li>
					<g:select class="dropdown" name="group-actions" from="${['Rename group', 'Delete group']}"
							keys="${['rename', 'delete']}"
							noSelection="${['': 'More actions...']}" />
				</li>
				<li>
					<g:remoteLink class="btn" controller="export" action="contactWizard" params="[groupId: contactsSection?.id, contactsSection:contactsSection instanceof frontlinesms2.Group ? 'group' : 'smartGroup']" onSuccess="launchSmallPopup('Export', data, 'Export')">Export</g:remoteLink>
				</li>
			</ul>
		</g:if>
		<g:else>
			<h3>${contactInstance?.name ?: contactInstance?.primaryMobile ?: 'New Contact'}</h3>
			<ul class="section-header-buttons button-list">
				<li>
					<g:remoteLink class="btn" controller="export" action="contactWizard" onSuccess="launchSmallPopup('Export', data, 'Export')">Export</g:remoteLink>
				</li>
			</ul>
		</g:else>
	</div>
</div>