<%@ page cntentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead/>
		<r:require module="search"/>
		<f:render template="/includes"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, search.moreoptions.label, popup.done, smallpopup.send, smallpopup.messages.export.title, wizard.quickmessage.title, smallpopup.export"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body id="search-tab">
		<div id="header">
			<div id="notifications">
				<f:render template="/system_notifications"/>
				<f:render template="/flash"/>
			</div>
			<f:render template="/system_menu"/>
			<f:render template="/tabs"/>
		</div>
        <div id="main">
			<f:render template="menu"/>
			<div id="content">
				<div id="message-list" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') ? '' : 'tall-header'}">
					<f:render template="/search/header"/>
					<f:render template="/message/message_list"/>
					<g:layoutBody/>
					<f:render template="/message/footer"/>
				</div>
				<f:render template="/message/message_details"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
