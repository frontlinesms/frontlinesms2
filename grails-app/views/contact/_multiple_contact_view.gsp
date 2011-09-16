<div class="multiple-contact hide">
	<div class="buttons">
		<ol>
			<li><g:actionSubmit id="update-all" action="updateMultipleContacts" value="Save All"/></li>
			<li><g:link class="cancel" action="list" default="Cancel">Cancel</g:link></li>
			<li>
				<a id="btn_delete_all" onclick="launchConfirmationPopup('Delete all');">
					Delete all
				</a>
			</li>
		</ol>
	</div>
	<div id="contact-count">&nbsp;</div>
	<div class="multiple-contact">
		<div>
			<ol id='multi-group-list'>
				<g:each in="${sharedGroupInstanceList}" status="i" var="g">
					<li class="${g == groupInstance ? 'selected' : ''}">
						<input type="text" name="${g.name}" value="${g.name}" disabled="true" />
						<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
					</li>
				</g:each>
				<li id="multi-no-groups" style="${sharedGroupInstanceList?'display: none':''}">
					<p>Not part of any Groups</p>
				</li>
			</ol>
		</div>
		<div id='multi-group-add' class="dropdown">
			<g:select name="multi-group-dropdown"
					noSelection="['_':'Add to group...']"
					from="${nonSharedGroupInstanceList}"
					optionKey="id"
					optionValue="name"/>
		</div>
	</div>
</div>