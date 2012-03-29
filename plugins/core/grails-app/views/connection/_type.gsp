<%@ page import="frontlinesms2.*" %>
<div id="tabs-1">
	<g:if test="${fconnectionInstance == null}">
		<ul id="type-list">
			<g:each in="${Fconnection.implementations}" status="i" var="it">
				<li>
					<g:set var="type" value="${it.shortName}"/>
					<g:radio class="${type}" name="connectionType"
							value="${type}" onclick="setConnectionType('${type}')"
							checked="${i == 0}"/>
					<g:message code="${it.simpleName.toLowerCase()}.label"/>
				</li>
			</g:each>
		</ul>
	</g:if>
</div>

