<div id='other-actions' class="actions buttons">
	<g:if test="${messageSection == 'poll'}">
		<div id="poll-actions">
			<div>
				<g:hiddenField name="owner-id" value="${ownerInstance.id}" />
				<g:hiddenField name="responseId" value="${responseInstance?.id}" />
				<select class="dropdown extra-msg-btn" name="categorise_dropdown" id="categorise_dropdown" onchange="categorizeClickAction()">
					<option value="na" class="na">Categorize response</option>
					<option value="btn-${responseInstance?.id}" class="na">${responseInstance?.value}</option>
					<g:each in="${responseList}" status="i" var="r">
						<g:if test="${r.id != responseInstance?.id}">
							<option value="btn-${r.id}" >${r.value}</option>
						</g:if>
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
