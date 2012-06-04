<%@ page cntentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead/>
		<r:require module="search"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, search.moreoptions.label, popup.done, smallpopup.send, smallpopup.messages.export.title, wizard.quickmessage.title, smallpopup.export, popup.ok, many.selected, message.character.count"/>
		<r:script>
			$(function() {  
			   disablePaginationControls();
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<div id="head">
			<fsms:render template="/tabs"/>
		</div>
		<div id="body" class="messages">
			<fsms:render template="menu"/>
			<g:form controller="${params.controller}"
					params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance?.id]">
				<g:hiddenField name="searchId" value="${search?.id}"/>
				<div id="list-container">
					<div id="list-head">
						<fsms:render template="/search/header"/>
					</div>
					<fsms:render template="/message/message_list"/>
					<div id="list-foot">
						<fsms:render template="/message/footer"/>
					</div>
				</div>
				<div id="detail">
					<fsms:render template="/message/message_details"/>
				</div>
			</g:form>
			</div>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>

