<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>
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
		<fsms:render template="message"/>
		<div id="tabs-2" class="${configureTabs.contains("tabs-2") ? "" : "hide"}">
			<fsms:render template="select_recipients"/>
		</div>
		<fsms:render template="confirm"/>
	</g:formRemote>
</div>

<script>
	function initializePopup() {
		$("#tabs-1").contentWidget({
			validate: function() {
				updateCount();
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
		$("#notifications").prepend("<div class='flash message'>" + data + "<a class='hide-flash'>x</a></div>");
	}
</script>
