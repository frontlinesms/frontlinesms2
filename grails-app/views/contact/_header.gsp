<div class="section-actions" id="contact-actions">
	<g:if test="${contactsSection instanceof frontlinesms2.Group || contactsSection instanceof frontlinesms2.SmartGroup}">
		<g:hiddenField name="groupId" value="&groupId=${contactsSection?.id}"/>
		<g:hiddenField name="contactSection" value="${contactsSection instanceof frontlinesms2.Group ? 'group' : 'smartGroup'}"/>
		<h3>${contactsSection.name}</h3>
		<ul>
			<li>
				<g:select name="group-actions" from="${['Rename group', 'Delete group']}"
						keys="${['rename', 'delete']}"
						noSelection="${['': 'More actions...']}"/>
			</li>
		</ul>
	</g:if>
	<g:elseif test="${!contactInstance}">
		<h3>New Group</h3>
	</g:elseif>
	<g:else>
		<h3>${contactInstance.name?:contactInstance.primaryMobile?:'New Contact'}</h3>
	</g:else>
</div>