<%@ page import="frontlinesms2.Webconnection" %>
<div id="webconnection-config">
	<g:if test="${activityInstanceToEdit?.id}">
		<fsms:render template="/webconnection/${activityInstanceToEdit?.class.type}/config"/>
		<g:hiddenField name="webconnectionType" value="${activityInstanceToEdit?.type}"/>
	</g:if>
	<g:else>
		<fsms:render template="/webconnection/${Webconnection.implementations[0].type}/config"/>
	</g:else>
</div>
