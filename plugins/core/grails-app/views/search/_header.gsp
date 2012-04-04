<div class="section-header ${messageSection}" id="message-list-header">
	<h3 class="search ${params.action == 'no_search' ? 'message' : 'activity'}">Search</h3>
	<ul class="header-buttons">
	 	<li><g:remoteLink class="section-action-button btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message">Quick message</div>
		</g:remoteLink></li>
		<g:if test="${search}">
 			<li id="export-btn">
	  			<g:remoteLink class="btn" controller="export" action="messageWizard" params='[messageSection: "${messageSection}", searchId: "${search?.id}"]' onSuccess="launchSmallPopup('Export Results (${messageInstanceTotal} messages)', data, 'Export');">
					Export results
				</g:remoteLink>
			</li>
		</g:if>
		<g:else>
			<li id="export-btn">
	  			<a class="btn" disabled="disabled">
					Export results
				</a>
			</li>
		</g:else>
	</ul>
	<g:if test="${searchDescription}">
		<p id="activity-details">${searchDescription}</p>
 	</g:if>
</div>
