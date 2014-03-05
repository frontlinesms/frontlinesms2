<div class="content search ${messageSection}">
	<h1 class="${params.action == 'no_search' ? 'message' : 'activity'}">
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
			<g:if test="${interactionInstanceTotal > 0}">
				<fsms:popup class="btn" controller="export" action="messageWizard"
						params="[messageSection:messageSection, searchId:search.id]"
						popupCall="launchSmallPopup(i18n('smallpopup.messages.export.title', '${interactionInstanceTotal}'), data, i18n('action.export'));">
					<g:message code="search.export"/>
				</fsms:popup>
			</g:if>
			<g:else>
	  			<a class="btn disabled">
					<g:message code="search.export"/>
				</a>
			</g:else>
		</li>
	</ul>
</div>

