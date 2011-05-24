<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="content-menu" id="messages-menu">
	<li>
		<h2>Create new...</h2>
		<ol id="create-submenu">
				<li id="create-poll">
					<g:link class="create" controller="poll" action="create">Poll</g:link>
				</li>
		</ol>
	</li>
	<li>
		<h2>Messages</h2>
		<ol>
			<li class="${(messageSection=='inbox')?'selected':''}">
				<g:link action="inbox">Inbox</g:link>
			</li>
			<li class="${(messageSection=='sent')? 'selected':''}">
				<g:link action="sent">Sent</g:link>
			</li>
		</ol>
	</li>
	<li>
		<h2>Activities</h2>
		<ol id="activities-submenu">
			<g:each in="${pollInstanceList}" status="i" var="p">
				<li>
					<g:link action="poll" id="${p.id}" class="${p == pollInstance ? 'selected' : ''}">${p.title}</g:link>
				</li>
			</g:each>
		</ol>
	</li>
 </ul>