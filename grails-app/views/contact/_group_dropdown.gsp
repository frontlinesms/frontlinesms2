<g:if test="contactInstance">
	<div id="group-section" class="field">
		<label for="groups">Groups</label>
		<div>
			<ol id='group-list'>
				<g:each in="${contactGroupInstanceList}" status="i" var="g">
					<li class="${g == groupInstance ? 'selected' : ''}">
						<input type="text" name="${g.name}" value="${g.name}" readonly="readonly" />
						<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
					</li>
				</g:each>
				<li id="no-groups" style="${contactGroupInstanceList?'display: none':''}">
					<p>Not part of any Groups</p>
				</li>
			</ol>
		</div>
	</div>
	<div id='group-add' class="dropdown">
		<select id="group-dropdown" name="group-dropdown">
			<option class="not-group">Add to group...</option>
			<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
				<option value="${g.id}">${g.name}</option>
			</g:each>
		</select>
	</div>
</g:if>
<g:else>
	<div>
		<ol id='multi-group-list'>
			<g:each in="${contactGroupInstanceList}" status="i" var="g">
				<li class="${g == groupInstance ? 'selected' : ''}">
					<input type="text" name="${g.name}" value="${g.name}" readonly="readonly" />
					<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
				</li>
			</g:each>
			<li id="multi-no-groups" style="${contactGroupInstanceList?'display: none':''}">
				<p>Not part of any Groups</p>
			</li>
		</ol>
	</div>
	<div id='multi-group-add' class="dropdown">
		<select id="multi-group-dropdown" name="multi-group-dropdown">
			<option class="not-group">Add to group...</option>
			<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
				<option value="${g.id}">${g.name}</option>
			</g:each>
		</select>
	</div>
</g:else> 
