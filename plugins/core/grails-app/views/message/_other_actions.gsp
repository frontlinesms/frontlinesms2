<div id='other-actions' class="actions buttons">
	<g:if test="${messageSection == 'activity' && ownerInstance?.type == 'poll'}">
		<div id="poll-actions">
			<div>
				<g:hiddenField name="owner-id" value="${ownerInstance.id}" />
				<g:hiddenField name="responseId" value="${responseInstance?.id}" />
				<select class="dropdown extra-msg-btn" name="categorise_dropdown" id="categorise_dropdown" onchange="categorizeClickAction()">
					<option value="na" class="na"><g:message code="activity.categorize" /></option>
					<g:each in="${ownerInstance.responses}" status="i" var="r">
						<option value="btn-${r.id}" >${r.value}</option>
					</g:each>
				</select>
			</div>
		</div>
	</g:if>
	<g:if test="${grailsApplication.config.frontlinesms.plugin == 'core'}">
		<g:render template="../message/move_message" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
	</g:if>
	<g:else>
		<g:render template="/message/move_message" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
	</g:else>
</div>
