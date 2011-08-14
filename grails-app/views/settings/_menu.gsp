<%@ page contentType="text/html;charset=UTF-8" %>
<ol class="context-menu" id="settings-menu">
	<li class="section">
		<ol class='sub-menu'>
       		<li class="${(settingsSection=='connections')? 'selected':''}">
       			<g:link url="${[controller:'settings', action:'connections']}">Phones & connections</g:link>
       		</li>
       	</ol>
	</li>
</ol>