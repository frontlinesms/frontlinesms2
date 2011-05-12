<g:if test="${flash.message}">
	<div class="flash message">${flash.message}</div>
</g:if>
<g:hasErrors bean="${contactInstance}">
	<div class="flash errors">
		<g:renderErrors bean="${contactInstance}" as="list"/>
	</div>
</g:hasErrors>