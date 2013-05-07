<div id="body-content-head">
	<div class="content">
		<h1><g:message code="connection.header"/></h1>
		<ul class="buttons">
			<li>
				<fsms:popup class="btn" name="addConnection" controller='connection' action="wizard" popupCall="mediumPopup.launchMediumWizard(i18n('connection.add'), data, i18n('wizard.create'), 675, 500, false)">
					<g:message code="connection.add" />
				</fsms:popup>
			</li>
		</ul>
	</div>
</div>
<div id="body-content" class="connections">
	<g:if test="${fconnectionInstanceTotal==0}">
		<p class="no-content"><g:message code="connection.list.none"/></p>
	</g:if>
	<g:else>
		<table class="connection-list">
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<fsms:render template="connection" model="[c:c]"/>
			</g:each>
		</table>
	</g:else>
	<fsms:render template="routing"/>
</div>
