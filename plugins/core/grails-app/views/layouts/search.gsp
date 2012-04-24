<%@ page cntentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead />
		<r:require module="search"/>
		<g:render template="/includes" plugin="core"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, search.moreoptions.label"/>
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
				<g:render template="/system_notifications" plugin="core"/>
				<g:render template="/flash" plugin="core"/>
			</div>
			<g:render template="/system_menu" plugin="core"/>
			<g:render template="/tabs" plugin="core"/>
		</div>
        <div id="main">
			<g:render template="menu" plugin="core"/>
			<div id="content">
				<div id="message-list" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') ? '' : 'tall-header'}">
					<g:render template="/search/header" plugin="core"/>
					<g:render template="/message/message_list" plugin="core"/>
					<g:layoutBody />
					<g:render template="../message/footer" plugin="core"/>
				</div>
				<g:render template="../message/message_details" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
