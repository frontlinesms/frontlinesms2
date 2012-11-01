<%@ page import="frontlinesms2.Webconnection" %>
<div id="webconnection-confirm">
	<g:if test="${activityInstanceToEdit?.id}">
		<fsms:render template="/webconnection/${activityInstanceToEdit?.class.type}/confirm"/>
	</g:if>
	<g:else>
		<fsms:render template="/webconnection/${Webconnection.implementations[0].type}/confirm"/>
	</g:else>
</div>
