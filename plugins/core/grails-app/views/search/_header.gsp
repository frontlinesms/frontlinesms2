<div class="content ${messageSection}">
	<h1 class="search ${params.action == 'no_search' ? 'message' : 'activity'}">
		<g:message code="search.header"/>
	</h1>
	<g:if test="${searchDescription}">
		<p class="description">${searchDescription}</p>
 	</g:if>
	<ul class="buttons">
	 	<li>
			<fsms:quickMessage class="section-action-button btn"/>
		</li>
 		<li id="export-btn">
			<g:if test="${search}">
				<g:remoteLink class="btn" controller="export" action="messageWizard"
						params="[messageSection:messageSection, searchId:search.id]"
						onSuccess="launchSmallPopup(i18n('smallpopup.messages.export.title', '${checkedMessageCount}'), data, i18n('action.export'));">
					<g:message code="search.export"/>
				</g:remoteLink>
			</g:if>
			<g:else>
	  			<a class="btn" disabled="disabled">
					<g:message code="search.export"/>
				</a>
			</g:else>
		</li>
	</ul>
</div>

