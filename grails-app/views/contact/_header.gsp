<div class="section-header" id="contact-header">
	<g:if test="${contactsSection instanceof frontlinesms2.Group || contactsSection instanceof frontlinesms2.SmartGroup}">
		<g:hiddenField name="groupId" value="${contactsSection?.id}"/>
		<g:hiddenField name="contactSection" value="${contactsSection instanceof frontlinesms2.Group ? 'group' : 'smartGroup'}"/>
		<h3>${contactsSection.name} (${contactInstanceTotal})</h3>
		<ul class="section-header-buttons button-list">
			<li>
				<g:select class="btn" name="group-actions" from="${['Rename group', 'Delete group']}"
						keys="${['rename', 'delete']}"
						noSelection="${['': 'More actions...']}"/>
			</li>
		</ul>
	</g:if>
	<g:else>
		<h3>${contactInstance?.name ?: contactInstance?.primaryMobile ?: 'New Contact'}</h3>
	</g:else>
</div>