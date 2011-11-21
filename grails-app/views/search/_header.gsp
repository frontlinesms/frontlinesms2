<div class="section-actions ${messageSection}" id="search-actions">
	<div class="activity-title">
		<h3>Search</h3>
		<ul class="section-actions-buttons button-list">
			<g:if test="${search}">
	 			<li id="export-btn">
		  			<g:remoteLink class="btn" controller="export" action="wizard" params='[messageSection: "${messageSection}", searchId: "${search?.id}"]' onSuccess="launchSmallPopup('Export Results (${messageInstanceTotal} messages)', data, 'Export');">
						Export results
					</g:remoteLink>
				</li>
			</g:if>
			<g:else>
				<li id="export-btn">
		  			<a class="btn disabled">
						Export results
					</a>
				</li>
			</g:else>
		</ul>
		<g:if test="${searchDescription}">
			<p id="activity-details">${searchDescription}</p>
	 	</g:if>
	</div>
</div>