<%@ page contentType="text/html;charset=UTF-8" %>
<ul id="main-tabs">
	<li><g:link class="tab ${params.controller=='message'?'selected':''}" url="${[controller:'message']}" id="goto-messages"">Messages</g:link></li>
	<li><g:link class="tab ${params.controller=='contact'?'selected':''}" url="${[controller:'contact']}" id="goto-contacts">Contacts</g:link></li>
	<li><g:link class="tab ${params.controller=='report'?'selected':''}" url="${[controller:'report']}" id="goto-reports">Reports</g:link></li>
	<li><g:link class="tab ${params.controller=='search'?'selected':''}" url="${[controller:'search']}" id="goto-search">Search</g:link></li>
	<li><g:link class="tab ${params.controller=='connection'?'selected':''}" url="${[controller:'settings']}" id="goto-settings">Settings & Plugins</g:link></li>
</ul>
