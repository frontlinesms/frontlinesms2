<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<div id="tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="quickmessage.validation.prompt"/></div>
	<ul>
		<g:each in="['tabs-1' : message(code: 'quickmessage.enter.message'), 'tabs-2' : message(code: 'quickmessage.select.recipients'), 'tabs-3' : message(code: 'quickmessage.confirm')]" var='entry'>
			<g:if test="${configureTabs.contains(entry.key)}">
				<li><a href="#${entry.key}">${entry.value}</a></li>
			</g:if>
		</g:each>
	</ul>

	<g:formRemote name="send-quick-message" url="${[action:'send', controller:'message']}" method="post" onSuccess="addFlashMessage(data)">
		<div id="tabs-1" class="${configureTabs.contains('tabs-1') ? '' : 'hide'}">
			<fsms:render template="/message/compose"/>
		</div>
		<div id="tabs-2" class="${configureTabs.contains("tabs-2") ? "" : "hide"}">
			<fsms:render template="/message/select_recipients"/>
		</div>
		<div id="tabs-3"  class="confirm ${configureTabs.contains('tabs-3') ? '' : 'hide'}">
			<fsms:render template="confirm"/>
		</div>
	</g:formRemote>
</div>

<r:script>
	$("#messageText").live("blur", function() {
		var value = $(this).val();
		if(value) {
			$("#confirm-message-text").html(value);
		} else {
			$("#confirm-message-text").html("none"); // FIXME i18n
		}
	});

	function initializePopup() {
		$("#tabs-1").contentWidget({
			validate: function() {
				updateMessageCount();
				return true;
			}
		});
		
		$("#tabs-2").contentWidget({
			validate: function() {
				addAddressHandler();
				return isGroupChecked("addresses");
			}
		});
	}

	function addFlashMessage(data) {
		$("#notifications .flash").remove();
		$("#notifications").prepend("<div class='flash message'>" + data + "<a class='hider hide-flash'>x</a></div>");
	}
</r:script>
