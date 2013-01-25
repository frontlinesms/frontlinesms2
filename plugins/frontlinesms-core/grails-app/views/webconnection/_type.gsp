<%@ page import="frontlinesms2.Webconnection" %>
<g:if test="${!activityInstanceToEdit?.id}">
	<div class="input">
		<label><g:message code="webconnection.select.type"/></label>
		<ul class="select radio">
			<g:each in="${Webconnection.implementations}" status="i" var="it">
				<li>
					<label>
						<h3><g:message code="webconnection.${it.type}.label"/></h3>
						<g:radio name="webconnectionType" checked="${it.type == 'generic'}"
								value="${it.type}" onchange="setType('${it.type}')"/>
						<fsms:info message="webconnection.${it.type}.description"/>
					</label>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>
