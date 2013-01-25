<%@ page import="frontlinesms2.*" %>
<g:if test="${!fconnectionInstance}">
	<div class="input">
		<label><g:message code="connection.select"/></label>
		<ul class="select radio">
			<g:each in="${Fconnection.implementations}" status="i" var="it">
				<li>
					<label>
						<h3><g:message code="${it.shortName}.label"/></h3>
						<g:radio name="connectionType" checked="${i == 0}"
								value="${it.shortName}" onclick="fconnection.setType('${it.shortName}')"/>
						<fsms:info class="${it.shortName}" message="${it.shortName}.description"/>
					</label>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>
<g:else>
	<g:hiddenField name="connectionType" value="${fconnectionInstance?.shortName}"/>
</g:else>

