<jqui:resources />
<g:remoteLink controller="quickMessage" action="create" params="[checkedMessageIds: params.checkedMessageIds]" onSuccess="launchWizard('Reply All', data);" class="quick_message">
		Reply All
</g:remoteLink>
<g:link action="deleteMessage" params="[messageSection: params.messageSection, checkedMessageIds: params.checkedMessageIds]">
Delete All
</g:link>
