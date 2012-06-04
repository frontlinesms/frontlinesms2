<%@ page contentType="text/html;charset=UTF-8" %>
<div id="body-menu" class="archive">
	<ul>
		<li>
			<ul class="submenu">
				<li class="${(messageSection == 'inbox')? 'selected':''} inbox">
					<g:link controller="archive" action="inbox">
						<g:message code="archive.inbox"/>
					</g:link>
				</li>
				<li class="${(messageSection == 'sent')? 'selected':''} sent">
					<g:link controller="archive" action="sent">
						<g:message code="archive.sent"/>
					</g:link>
				</li>
				<li class="${(messageSection == 'activity') ? 'selected':''} activity">
					<g:link controller="archive" action="activityList">
						<g:message code="archive.activity"/>
					</g:link>
				</li>
				<li class="${(messageSection == 'folder')? 'selected':''} folder">
					<g:link controller="archive" action="folderList">
						<g:message code="archive.folder"/>
					</g:link>
				</li>
			</ul>
		</li>
	</ul>        
</div>                                                                                                                    

