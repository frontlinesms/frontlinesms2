<g:each in="${frontlinesms2.SystemNotification.findAllByRead(false)}">
	<div class="system-notification">
		${it.text}
		<g:remoteLink controller="systemNotification"
				action="markRead" id="${it.id}"
				class="hide-flash">x</g:remoteLink>
	</div>
</g:each>
