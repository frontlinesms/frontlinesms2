<ol class="context-menu" id='search-menu'>
	<g:form name="searchDetails">
		<li id='search-field' class="section">
			<h2 id="search-string">Keyword or phrase:</h2>
			<ol class='sub-menu'>
				<li class='field'>
					<g:textField name="searchString" id="searchString" value="${searchString}"/>
				</li>
			</ol>
		</li>
		<li class='section' id="search-filters">
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
		</li>
		<li class='section' class="buttons">
			<ol class='sub-menu'>
				<li>
					<g:actionSubmit class="search" name="result" value="${message(code: 'default.button.search.label', default: 'Search')}" action="result"/>
				</li>
			</ol>
		</li>
	</g:form>
</ol>
