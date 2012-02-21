<g:each in="${frontlinesms2.SystemNotification.findAllByRead(false)}">
	<div class="system-notification">
		${it.text}
		<g:remoteLink controller="systemNotification" action="markRead" id="${it.id}">mark read</g:remoteLink>
	</div>
</g:each>

<g:javascript>
	$(function() {
		$('.system-notification').find('a').live("click", function() {
			$(this).parent().slideUp(500);
			return true;
		});
	});
	
	function refreshSystemNotifications() {
		var notificationsHolder = $("#notifications")
		notificationsHolder.empty()
		$.getJSON("${createLink(controller:'systemNotification', action:'list')}", function(data) {
			var shouldRefresh = false
			$.each(data, function(key, notification) {
				var systemNotification = "<div class='system-notification'>" + notification.text + notification.markRead + "</div>";
				notificationsHolder.append(systemNotification);
				if(notification.text.indexOf("Created") != -1) {
					shouldRefresh = true
				}
			});
			
			if(shouldRefresh) {
				reloadConnectionList()
			}
		});
	}
	
	function reloadConnectionList() {
		$.get("${createLink(controller:'settings', action:'connections')}", function(data) {
			$('#connections').replaceWith($(data).find('#connections'));
		});
	}
</g:javascript>
