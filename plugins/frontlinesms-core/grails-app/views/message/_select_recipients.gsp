<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<div>
		<fsms:recipientSelector class="customactivity-field" groups="${groups}" smartGroups="${smartGroups}" contacts="${contacts}" addresses="${addresses}" />
	</div>
	<div class="controls">
		<div id="recipients-selected"><span id="recipient-count">0</span> <g:message code="quickmessage.selected.recipients"/></div>
	</div>
</div>

