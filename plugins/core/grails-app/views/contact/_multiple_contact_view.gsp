<div id="multiple-contacts" class="multiple-contact" style="display:none">
	<div id="action-buttons" class="buttons">
			<g:actionSubmit class="btn" id="update-all" action="updateMultipleContacts" value="${g.message(code:'contact.save.many')}" disabled="disabled" />
			<g:link class="cancel btn" disabled="disabled"><g:message code="contact.cancel"/></g:link>
			<a class="btn" id="btn_delete_all" onclick="launchConfirmationPopup(i18n('contact.delete.many'));">
				<g:message code="contact.delete.many"/>
			</a>
	</div>
	<div id="multiple-contact-info">
		<div id="checked-contact-count">&nbsp;</div>
		<ul id='multi-group-list'>
			<g:each in="${sharedGroupInstanceList}" status="i" var="g">
				<li id="${g.name}" class="${g == groupInstance ? 'selected' : ''}">
					<span>${g.name}</span>
					<a class="remove-group" id="remove-group-${g.id}"></a>
				</li>
			</g:each>
		</ul>
		<div id='multi-group-add'>
			<g:select name="multi-group-dropdown"
					class="dropdown"
					noSelection="['_':g.message(code:'contact.add.to.group')]"
					from="${nonSharedGroupInstanceList}"
					optionKey="id"
					optionValue="name"/>
		</div>
	</div>
</div>
