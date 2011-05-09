<%@ page contentType="text/html;charset=UTF-8" %>

<g:if test="${contactInstanceTotal > 0}">
	<ol id="contacts">
		<g:each in="${contactInstanceList}" status="i" var="c">
		  <g:if test="${c.name == ''}">
			<li> class="${c == contactInstance ? 'selected' : ''}"><g:link action="show" id="${c.id}">[No Name]</g:link></li>
		  </g:if>
		  <g:else>
			<li class="${c == contactInstance ? 'selected' : ''}"><g:link action="show" id="${c.id}">${c.name}</g:link></li>
		  </g:else>
		</g:each>
	</ol>
</g:if>
<g:else>
	<div id="contacts">
		You have no contacts saved
	</div>
</g:else>
