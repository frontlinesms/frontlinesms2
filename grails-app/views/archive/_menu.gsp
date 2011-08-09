<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="content-menu" id="archive-menu">
	<li>
		<ol>
				<li>
					<g:link class="${(messageSection=='inbox')? 'selected':''}" action="inbox" elementId="inbox" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Inbox Archive
					</g:link>
				</li>
				<li>
					<g:link class="${(messageSection=='sent')? 'selected':''}" action="sent" elementId="sent" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Sent Archive
					</g:link>
				</li>
				<li>
					<g:remoteLink elementId="poll" controller="poll" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Activity archive
					</g:remoteLink>
				</li>
		</ol>
	</li>
 </ul>
        
