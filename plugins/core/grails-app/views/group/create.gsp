<%@ page contentType="text/html;charset=UTF-8" %>
<div class="error-panel hide" id="smallpopup-error-panel"><div id="error-icon"></div></div>
<g:formRemote name="group-details" url="[controller: 'group', action:'save']" method="post" onSuccess="checkResults(data)">
	<table>
		<fsms:input instance="${groupInstance}" field="name" fieldPrefix="" table="true"/>
	</table>
</g:formRemote>