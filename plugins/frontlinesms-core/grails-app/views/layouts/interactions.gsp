<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<r:require module="messages"/>
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
			<fsms:render template="/interaction/menu"/>
			<g:form controller="${params.controller}"
					params="[messageId: messageInstance?.id, searchId: search?.id]">
				<g:hiddenField name="messageSection" value="${messageSection}"/>
				<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
				<div id="main-list-container">
					<div id="main-list-head">
						<fsms:render template="/interaction/header"/>
					</div>
					<fsms:render template="/interaction/message_list"/>
					<div id="main-list-foot">
						<fsms:render template="/interaction/footer"/>
					</div>
				</div>
				<g:layoutBody/>
				<div id="detail">
					<fsms:render template="/interaction/message_details"/>
				</div>
			</g:form>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>

