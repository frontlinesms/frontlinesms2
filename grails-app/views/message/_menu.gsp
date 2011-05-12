<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="content-menu" id="messages-menu">
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
 </ul>