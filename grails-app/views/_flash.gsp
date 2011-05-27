<g:if test="${flash.message}">
	<div class="flash message">${flash.message}</div>
</g:if>

<g:hasErrors bean="${contactInstance}">
	<div class="flash errors">
		<g:renderErrors bean="${contactInstance}" as="list"/>
	</div>
</g:hasErrors>

<g:hasErrors bean="${groupInstance}">
	<div class="flash errors">
		<g:renderErrors bean="${groupInstance}" as="list"/>
	</div>
</g:hasErrors>

<g:hasErrors bean="${pollInstance}">
	<div class="flash errors">
		<g:renderErrors bean="${pollInstance}" as="list"/>
	</div>
</g:hasErrors>
