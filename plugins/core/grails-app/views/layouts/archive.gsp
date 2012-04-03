<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Archive"/></title>
		<g:layoutHead />
		<r:require module="archive"/>
		<g:render template="/includes"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body id="archive-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
        </div>
		<div id="main" class="main">
			<g:render template="../archive/menu"/>
			<div id="content" class="content">
				<g:if test="${viewingMessages}">
					<g:render template="../message/header"/>
				</g:if>
				<g:else>
					<g:render template="header"/>
				</g:else>
				<div class="content-body">
					<g:if test="${(messageSection == 'activity') && !viewingMessages}">
						<g:render template="archived_activity_list"/>
					</g:if>
					<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
						<g:render template="archived_folder_list"/>
					</g:elseif>
					<g:else>
						<g:render template="../message/message_list"/>
					</g:else>
					<g:layoutBody />
				</div>
				<g:render template="../message/footer"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
