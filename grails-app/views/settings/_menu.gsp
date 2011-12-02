<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
	<ul class="main-list" id="settings-menu">
		<li class="section">
			<ul class='sublist'>
	       		<li class="${(settingsSection=='connections')? 'selected':''}">
	       			<g:link url="${[controller:'settings', action:'connections']}">Phones & connections</g:link>
	       		</li>
	       	</ul>
		</li>
	</ul>
</div>