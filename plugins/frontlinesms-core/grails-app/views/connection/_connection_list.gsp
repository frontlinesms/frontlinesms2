<div id="body-content-head">
	<div class="content">
		<h1><g:message code="connection.header"/></h1>
		<ul class="buttons">
			<li>
				<fsms:popup class="btn" name="addConnection" controller='connection' action="wizard" method="mediumPopup.launchMediumWizard(i18n('connection.add'), data, i18n('wizard.create'), 675, 500, false)">
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
		<ul>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="connection ${c == connectionInstance ? 'selected' : ''}">
					<fsms:render template="connection" model="[c:c]"/>
					<g:if test="${c == connectionInstance}">
						<%-- TODO when sanchez template rendering taglib is included, refactor here to use
							the newly-refactored selected_connection templates --%>
						<fsms:render template="selected_connection" model="[c:c]"/>
					</g:if>
				</li>
			</g:each>
		</ul>
	</g:else>
</div>

