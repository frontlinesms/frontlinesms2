<g:each in="${frontlinesms2.SystemNotification.findAllByRead(false)}">
	<div class="system-notification" id="notification-${it.id}">
		${it.text} <g:remoteLink controller="systemNotification" action="markRead" id="${it.id}">mark read</g:remoteLink>
	</div>
</g:each>

<g:javascript>
	$(function() {
		$('.system-notification').find('a').live("click", function() {
			$(this).parent().slideUp(500);
			return true;
		});
	});
</g:javascript>
