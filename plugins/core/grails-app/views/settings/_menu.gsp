<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
	<ul class="main-list" id="settings-menu">
		<li class="section">
			<ul class='sublist'>
				<li class="${params.action=='general' ? 'selected' : ''}">
	       			<g:link url="${[controller:'settings', action:'general']}">General</g:link>
	       		</li>
	       		<li class="${params.action=='connections' ? 'selected' : ''}">
	       			<g:link url="${[controller:'settings', action:'connections']}">Phones & connections</g:link>
	       		</li>
	       		<li class="${params.action=='logs' ? 'selected' : ''}">
	       			<g:link url="${[controller:'settings', action:'logs']}">System Log</g:link>
	       		</li>
	       	</ul>
		</li>
	</ul>
</div>
