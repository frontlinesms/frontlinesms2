<div class="multiple-contact hide">
	<g:form name="multiple-contact-details">
		<g:hiddenField name="contactIds" value=""/>
		<g:hiddenField name="groupsToAdd" value=","/>
		<g:hiddenField name="groupsToRemove" value=","/>
		<div class="buttons">
			<ol>
				<li> <g:actionSubmit class="save" id="btn_save_all" action="updateMultipleContacts" value="${message(code: 'default.button.save.label', default: 'Save')}"/></li>
				<li> <g:link class="cancel" action="list" default="Cancel">Cancel</g:link></li>
				<li> <g:actionSubmit id="btn_delete_all" class="delete" action="deleteContact" value="Delete All" onclick="return confirm('Delete ' + countCheckedContacts() + ' contacts')"/></li>
			</ol>
		</div>
		<div id="count"></div>
		<g:render template="group_dropdown"/>
	</g:form>
</div>
