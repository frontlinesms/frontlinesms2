<input type="hidden" value="${ignoreWords?:''}" id="ignoreWords" name="ignoreWords"/>
<g:submitToRemote url="[controller:'radioShow', action:'wordCloudStats', params:[id:ownerInstance?.id]]"
		onSuccess="showWordCloud(data)" class="btn" name="show-wordcloud-btn"
		value="${message(code:'wordcloud.show.cloud')}"/>
<a onclick="resetWordcloud()" id="reset-words-btn" class="btn" style="display:none;"><g:message code="wordcloud.reset.words"/></a>
<a onclick="showMessages()" id="show-messages-btn" class="btn" style="display:none;"><g:message code="wordcloud.show.messages"/></a>
