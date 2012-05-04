<%@ page cntentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead/>
		<r:require module="search"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, search.moreoptions.label, popup.done, smallpopup.send, smallpopup.messages.export.title, wizard.quickmessage.title, smallpopup.export, popup.ok"/>
		<r:script>
			$(function() {  
			   disablePaginationControls();
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body id="search-tab">
		<div id="header">
			<div id="notifications">
				<fsms:render template="/system_notifications"/>
				<fsms:render template="/flash"/>
			</div>
			<fsms:render template="/system_menu"/>
			<fsms:render template="/tabs"/>
		</div>
        <div id="main">
			<fsms:render template="menu"/>
			<div id="content">
				<div id="message-list" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') ? '' : 'tall-header'}">
					<fsms:render template="/search/header"/>
					<fsms:render template="/message/message_list"/>
					<g:layoutBody/>
					<fsms:render template="/message/footer"/>
				</div>
				<fsms:render template="/message/message_details"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
