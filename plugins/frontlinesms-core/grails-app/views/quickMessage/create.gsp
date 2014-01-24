<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<div id="tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="quickmessage.validation.prompt"/></div>
	<ul>
		<g:each in="['tabs-1' : message(code: 'quickmessage.enter.message')]" var='entry'>
			<g:if test="${configureTabs.contains(entry.key)}">
				<li><a href="#${entry.key}">${entry.value}</a></li>
			</g:if>
		</g:each>
	</ul>

	<g:formRemote name="send-quick-message" url="${[action:'send', controller:'message']}" method="post" onSuccess="addFlashMessage(data)">
		<div id="tabs-1" class="${configureTabs.contains('tabs-1') ? '' : 'hide'}">
			<div class="info">
				<p><g:message code="quickmessage.instructions"/></p>
			</div>
			<fsms:recipientSelector class="customactivity-field" explanatoryText="${true}" groups="${groups}" smartGroups="${smartGroups}" contacts="${contacts}" addresses="${addresses}" />
			<h2 class="quickmessage-composer-instruction"><g:message code="quickmessage.recipients.instructions"/></h2>
			<fsms:messageComposer name="messageText" rows="3" textAreaId="sendMessageText" target="sendMessageText" controller="quickMessage" value="${activityInstanceToEdit?activityInstanceToEdit.sentMessageText:messageText}"/>
			<fsms:render template="confirm"/>
		</div>
	</g:formRemote>
</div>

<r:script>
	function initializePopup() {
		$('ul.ui-tabs-nav,buttom#disabledBack').remove();
		messageComposerUtils.init($('div.message-composer').parent());
		$('select[name=recipients],textarea[name=messageText').change(updateRecipientAndMessageStats);
		$("#tabs-1").contentWidget({
			validate: function() {
				var value = $("textarea[name=messageText]").val().htmlEncode();
				return (recipientSelecter.getRecipientCount() > 0 && value.length > 0);
			}
		});
		updateRecipientAndMessageStats()
	}

	function updateRecipientAndMessageStats() {
		var recipientCount = recipientSelecter.getRecipientCount();
		$('#contacts-count').html(recipientCount);
	}

	function addFlashMessage(data) {
		$("#notifications .flash").remove();
		$("#notifications").prepend("<div class='flash message'>" + data + "<a class='hider hide-flash'>x</a></div>");
	}
</r:script>

