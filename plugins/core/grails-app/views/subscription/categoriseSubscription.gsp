<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<div>
	<g:form name="categorize_subscription" controller="message" action="move">
		<p class="info"><g:message code="subscription.categorize.description"/></p>
		<g:select class="dropdown" id="group_dropdown"
							from="${contactGroupInstanceList}"
							optionKey="key" optionValue="value"
							noSelection="Select Group..."
							onchange="\$(this).parent().submit()" />
		<g:link id='btn_join' class="buttons" controller='subscription' action='' >
		<g:link id='btn_leave' class="buttons" controller='subscription' action='' >
	</g:form>
</div>
