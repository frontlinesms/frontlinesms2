<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			
			function initializePopup() {
				$("#submit").attr('disabled', 'disabled');
			}
			
			function setChecked(activityType) {
				$("#activity-list input[checked=checked]").attr('checked', '');
				$("#activity-list ." + activityType).attr('checked', 'checked');
				$("#submit").removeAttr('disabled');
			}
		</script>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
	</head>
	<body>
		<form>
			<h2 class="bold">Create new activity:</h2>
			<ul id="activity-list">
				<li class="activity-list-item">
					<input type="radio" name="activity" value="announcement" class="announcement" onclick="setChecked('announcement')" /><span class="activity-choice">Announcement</span>
					<div class="activity-description">Send an announcement message and organize the responses</div>
				</li>
				<li class="activity-list-item">
					<input type="radio" name="activity" value="poll" class="poll" onclick="setChecked('poll')" /><span class="activity-choice">Poll</span>
					<div class="activity-description">Send a question and analyze the responses</div>
				</li>
				<li class="activity-list-item">
					<input type="radio" name="activity" value="autoreply" class="autoreply" onclick="setChecked('autoreply')" /><span class="activity-choice">Autoreply</span>
					<div class="activity-description">Automatically respond to incoming messages</div>
				</li>
			</ul>
		</form>
	</body>
</html>
