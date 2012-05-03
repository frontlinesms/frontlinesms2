<div id="message-detail">
	<g:hiddenField name="checkedMessageList" id="checkedMessageList" value="${checkedMessageList}"/>
	<g:if test="${grailsApplication.config.frontlinesms.plugin == 'core'}">
		<f:render template="../message/single_message_details"/>
		<f:render template="../message/multiple_message_details"/>	
	</g:if>
	<g:else>
		<f:render template="/message/single_message_details"/>
		<f:render template="/message/multiple_message_details"/>
	</g:else>
</div>
