<div id="message-detail">
	<g:hiddenField name="checkedMessageList" id="checkedMessageList" value="${checkedMessageList}" />
	<g:if test="${grailsApplication.config.frontlinesms.plugin == 'core'}">
		<g:render template="/message/single_message_details" />
		<g:render template="/message/multiple_message_details" />	
	</g:if>
	<g:else>
		<g:render template="/message/single_message_details" plugin="core"/>
		<g:render template="/message/multiple_message_details" plugin="core"/>
	</g:else>
</div>
