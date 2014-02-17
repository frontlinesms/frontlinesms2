<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:message code="archive.header"/> <g:layoutTitle/></title>
		<g:layoutHead/>
		<r:require module="archive"/>
		<fsms:render template="/includes"/>
		<fsms:i18nBundle/>
		<r:script>
			$(function() {  
				disablePaginationControls();
				$(window).resize(new Resizer('#main-list-container', '#main-list-head', '#main-list-foot'));
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<fsms:render template="/head"/>
		<div id="body" class="messages">
			<fsms:render template="/archive/menu"/>
			<g:form controller="${params.controller}"
					params="[messageId:messageInstance?.id, searchId:search?.id]">
				<g:hiddenField name="messageSection" value="${messageSection}"/>
				<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
				<div id="main-list-container">
					<div id="main-list-head">
						<g:if test="${viewingMessages}">
							<fsms:render template="/interaction/header"/>
						</g:if>
						<g:else>
							<fsms:render template="/archive/header"/>
						</g:else>
					</div>
					<g:if test="${messageSection == 'activity' && !viewingMessages}">
						<fsms:render template="archived_activity_list"/>
					</g:if>
					<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
						<fsms:render template="archived_folder_list"/>
					</g:elseif>
					<g:elseif test="${(messageSection == 'inbox' || messageSection == 'sent') && !viewingMessages}">
						<fsms:render template="/interaction/message_list"/>
					</g:elseif>
					<g:elseif test="${!viewingMessages}">
						<fsms:render template="/${messageSection}/archived_${messageSection}_list"/>
					</g:elseif>
					<g:else>
						<fsms:render template="/interaction/message_list"/>
					</g:else>
					<div id="main-list-foot">
						<fsms:render template="/interaction/footer"/>
					</div>
				</div>
				<g:layoutBody/>
				<div id="detail">
					<fsms:render template="/interaction/message_details" />
				</div>
			</g:form>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>
