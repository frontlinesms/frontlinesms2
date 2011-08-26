<div class="multiple-contact hide">
	<g:form name="multiple-contact-details">
		<g:hiddenField name="contactIds" value=""/>
		<g:hiddenField name="groupsToAdd" value=","/>
		<g:hiddenField name="groupsToRemove" value=","/>
		<g:if test="${contactsSection instanceof frontlinesms2.Group}">
			<g:hiddenField name="groupId" value="${contactsSection.id}"/>
		</g:if>
		<div class="buttons">
			<ol>
				<li> <g:actionSubmit class="save" id="btn_save_all" action="updateMultipleContacts" value="${message(code: 'default.button.save.label', default: 'Save')}"/></li>
				<li> <g:link class="cancel" action="list" default="Cancel">Cancel</g:link></li>
				<li> <g:actionSubmit id="btn_delete_all" class="delete" action="deleteContact" value="Delete All" onclick="return confirm('Delete ' + countCheckedContacts() + ' contacts')"/></li>
			</ol>
		</div>
		<div id="count"></div>
		<div class="multiple-contact">
			<div>
				<ol id='multi-group-list'>
					<g:each in="${sharedGroupInstanceList}" status="i" var="g">
						<li class="${g == groupInstance ? 'selected' : ''}">
							<input type="text" name="${g.name}" value="${g.name}" readonly="readonly" />
							<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
						</li>
					</g:each>
					<li id="multi-no-groups" style="${sharedGroupInstanceList?'display: none':''}">
						<p>Not part of any Groups</p>
					</li>
				</ol>
			</div>
			<div id='multi-group-add' class="dropdown">
				<select id="multi-group-dropdown" name="multi-group-dropdown">
					<option class="not-group">Add to group...</option>
					<g:each in="${nonSharedGroupInstanceList}" status="i" var="g">
						<option value="${g.id}">${g.name}</option>
					</g:each>
				</select>
			</div>
		</div>
	</g:form>
</div>
