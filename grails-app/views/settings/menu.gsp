<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">General</a></li>
		<li><a href="#tabs-2">Phones & Connections</a></li>
	</ol>
	<g:render template="general"/>
	<g:render template="/connection/connection_list"/>
</div>