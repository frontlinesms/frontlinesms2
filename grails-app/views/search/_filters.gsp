<div id='filters'>
	<g:if test="${groupInstanceList || pollInstanceList}">
		<h2>Limit Search to:</h2>
		<ol class="sub-menu">
			<li class='field'>
				<g:select name="groupId" from="${groupInstanceList}" value="${groupInstance?.id}"
						  optionKey="id" optionValue="name"
						  noSelection="${['':'Select group']}"/>
			</li>
			<li class="field">
				<g:select name="activityId" from="${pollInstanceList + folderInstanceList}"
						  value="${activityId}"
						  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
						  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
						  noSelection="${['':'Select activity / folder']}"/>
			</li>
		</ol>
	</g:if>
	<ol class="sub-menu" id="added-options">
		<li class="field" id='contact-name-field'>
			<img src='${resource(dir:'images/icons', file:'contacts.gif')}'" /><h2>Contact Name:</h2>
			<g:textField name="contactSearchString" id="contactSearchString" value="${contactInstance}"/>
		</li>
	</ol>
	<h2>
		<a id="more-search-options" onclick="expandOptions()"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
		More search options
	</h2>
	<ol class="sub-menu" id="expanded-search-options">
		<li><a id='contact-name' onclick="addContactField()">Contact name</a></li>
	</ol>
</div>