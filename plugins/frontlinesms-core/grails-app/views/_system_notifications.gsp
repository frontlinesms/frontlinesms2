<g:each in="${frontlinesms2.SystemNotification.findAllByRd(false)}">
	<div class="system-notification" id="notification-${it.id}">
		${it.text}
		<a onclick="systemNotification.hide(${it.id})" class="hider">x</a>
	</div>
</g:each>
