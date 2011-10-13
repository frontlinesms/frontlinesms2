<div id="tabs-2">
<g:javascript src="group/generate-message.js"/>
	<p>An automatic reply can be sent when a message is received requesting to join or leave a group.</p>
	<div>
		<h2>Join Automatic Reply</h2>
		<g:checkBox name="reply" value="joinReplyMessage" id="join_reply_checkbox" checked='false'/> Send an automatic reply to join requests
		<g:textField name="joinReplyMessage" class="check-bound-text-area" checkbox_id="join_reply_checkbox"/>
		<img src='${resource(dir:'images/icons',file:'magic_stick.png')}' onclick="generateMessage('join_reply')" />
	</div>
	
	<div>
		<h2>Leave Automatic Reply</h2>
		<g:checkBox name="reply" value="leaveReplyMessage" id="leave_reply_checkbox" checked='false'/> Send an automatic reply to leave requests
		<g:textField name="leaveReplyMessage" class="check-bound-text-area" checkbox_id="leave_reply_checkbox"/>
		<img src='${resource(dir:'images/icons',file:'magic_stick.png')}' onclick="generateMessage('leave_reply')"/>
	</div>

</div>