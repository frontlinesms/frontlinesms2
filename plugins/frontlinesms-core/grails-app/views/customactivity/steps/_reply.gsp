<fsms:step type="reply" stepId="${stepId}">
<div class='input'>
	<fsms:messageComposer name="autoreplyText" rows="3" target='autoreplyText${stepId}${random}'/>
	<%--
	<g:textArea id="autoreplyText${stepId}${random}" name="autoreplyText" rows="3" value="${autoreplyText}"/>
	<div class="controls" style="visibilty:hidden;">
		<div class="stats">
			<span id="send-message-stats" class="character-count">
				<g:message code="message.character.count" args="[0, 1]"/>
			</span>
		</div>
		<div class="stats character-count-warning" style="display:none;">
			<g:message code="message.character.count.warning"/>
		</div>
		<fsms:magicWand target="autoreplyText${stepId}${random}" controller="autoreply" hidden="true"/>
	</div>
	--%>
</div>
</fsms:step>
<%--
<r:script>
	$("textarea[id^=autoreplyText]").live("keyup", updateSmsCharacterCount);
	$("textarea[id^=autoreplyText]").live("focus", function() {
		$(this).siblings(".controls").show(300);
		magicwand.reset($(this).siblings(".controls").find("select"));
	});
	$("textarea[id^=autoreplyText]").live('blur', function() {
		console.log($(this));
		$(this).siblings('.controls').hide(300);
	});
</r:script>
--%>
