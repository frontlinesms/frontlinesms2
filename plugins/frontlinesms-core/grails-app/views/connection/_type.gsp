<%@ page import="frontlinesms2.*" %>
<g:if test="${!fconnectionInstance}">
	<div class="input">
		<label><g:message code="connection.select"/></label>
		<ul class="select radio">
			<g:each in="${Fconnection.implementations}" status="i" var="it">
				<li>
					<label>
						<g:message code="${it.simpleName.toLowerCase()}.label"/>
						<g:radio name="connectionType" checked="${i == 0}"
								value="${it.shortName}" onclick="fconnection.setType('${it.shortName}')"/>
						<p class="info ${it.shortName}"><g:message code="${it.shortName}fconnection.description"/></p>
					</label>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>
<g:else>
	<g:hiddenField name="connectionType" value="${fconnectionInstance?.shortName}"/>
</g:else>

