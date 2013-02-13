<fsms:step type="reply" stepId="${stepId}">
<div class='input'>
	<fsms:messageComposer name="autoreplyText" rows="3" textAreaId="autoreplyText${stepId}${random}" target="autoreplyText${stepId}${random}" controller="autoreply"/>
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
<script src="http://www.jacklmoore.com/autosize/jquery.autosize.js"></script>
<r:script>
	$(function() {
		$('.message-composer').live('focus', function() {
			$(this).addClass('focus');
		});

		$('.message-composer').live('blur', function() {
			$(this).removeClass('focus');
		});
	});
</r:script>
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
