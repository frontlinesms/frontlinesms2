<%@ page import="frontlinesms2.Webconnection" %>
<div id="webconnection-config">
	<g:if test="${activityInstanceToEdit?.id}">
		<fsms:render template="/webconnection/${activityInstanceToEdit?.class.type}/config"/>
	</g:if>
	<g:else>
		<fsms:render template="/webconnection/generic/config"/>
	</g:else>
</div>
