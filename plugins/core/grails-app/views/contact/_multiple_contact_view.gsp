<div class="multiple-contact hide">
	<div id="action-buttons" class="buttons">
			<g:actionSubmit class="btn" id="update-all" action="updateMultipleContacts" value="${g.message(code:'contact.button.saveall')}" disabled="disabled"/>
			<g:link class="cancel btn" disabled="disabled"><g:message code="contact.button.cancel" /></g:link>
			<a class="btn" id="btn_delete_all" onclick="launchConfirmationPopup('Delete all');">
				<g:message code="contact.button.deleteall" />
			</a>
	</div>
	<div id="multiple-contact-info">
		<div id="contact-count">&nbsp;</div>
		<ul id='multi-group-list'>
			<g:each in="${sharedGroupInstanceList}" status="i" var="g">
				<li id="${g.name}" class="${g == groupInstance ? 'selected' : ''}">
					<span>${g.name}</span>
					<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
				</li>
			</g:each>
		</ul>
		<div id='multi-group-add'>
			<g:select class="dropdown" name="multi-group-dropdown"
					noSelection="['_':${g.message(code:"contact.select.addtogroup")}]"
					from="${nonSharedGroupInstanceList}"
					optionKey="id"
					optionValue="name"/>
		</div>
	</div>
</div>
