<div class="multiple-contact hide">
	<div class="buttons">
		<ol>
			<li><g:actionSubmit id="update-all" action="updateMultipleContacts" value="Save All"/></li>
			<li> <g:link class="cancel" action="list" default="Cancel">Cancel</g:link></li>
			<li>
				<a id="btn_delete_all" onclick="launchConfirmationPopup('Delete all');">
					Delete all
				</a>
			</li>
		</ol>
	</div>
	<div id="count"></div>
	<div class="multiple-contact">
		<div>
			<ol id='multi-group-list'>
				<g:each in="${sharedGroupInstanceList}" status="i" var="g">
					<li class="${g == groupInstance ? 'selected' : ''}">
						<input type="text" name="${g.name}" value="${g.name}" disabled="true" />
						<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
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
</div>