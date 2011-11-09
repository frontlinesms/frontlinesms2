<g:each in="${frontlinesms2.SystemNotification.findAllByRead(false)}">
	<div class="system-notification" style="border:dotted red 10px">
		${it.text}
		<g:remoteLink controller="systemNotification" action="markRead" id="${it.id}">mark read</g:remoteLink>
	</div>
</g:each>

<g:javascript>
	$(function() {
		$('.system-notification a').click(function() {
			$(this).parent().slideUp(500);
			return true;
		});
	});
</g:javascript>