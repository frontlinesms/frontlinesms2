<%@ page import="frontlinesms2.*" %>
<g:if test="${!fconnectionInstance}">
	<div class="input">
		<label for="pollType">XXX Select the type of thing</label>
		<ul class="select">
			<g:each in="${Fconnection.implementations}" status="i" var="it">
				<li>
					<label for="connectionType"><g:message code="${it.simpleName.toLowerCase()}.label"/></label>
					<g:radio name="connectionType" checked="${i == 0}"
							value="${it.shortName}" onclick="fconnection.setType('${it.shortName}')"/>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>

