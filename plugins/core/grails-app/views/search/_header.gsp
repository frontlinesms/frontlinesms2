<div class="section-header ${messageSection}" id="message-list-header">
	<h3 class="search ${params.action == 'no_search' ? 'message' : 'activity'}">
		<g:message code="search.header" />
	</h3>
	<ul class="header-buttons">
	 	<li><g:remoteLink class="section-action-button btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard(i18n('wizard.quickmessage.title'), data, i18n('smallpopup.send'), true);" id="quick_message">
			<div id="quick-message"><g:message code="search.quickmessage" /></div>
		</g:remoteLink></li>
		<g:if test="${search}">
 			<li id="export-btn">
	  			<g:remoteLink class="btn" controller="export" action="messageWizard" params='[messageSection: "${messageSection}", searchId: "${search?.id}"]' onSuccess="launchSmallPopup(i18n('smallpopup.messages.export.title', '${[checkedMessageCount]}'), data, i18n('smallpopup.export'));"> 
				<g:message code="search.export" />
				</g:remoteLink>
			</li>
		</g:if>
		<g:else>
			<li id="export-btn">
	  			<a class="btn" disabled="disabled">
					<g:message code="search.export" />
				</a>
			</li>
		</g:else>
	</ul>
	<g:if test="${searchDescription}">
		<p id="activity-details">${searchDescription}</p>
 	</g:if>
</div>
