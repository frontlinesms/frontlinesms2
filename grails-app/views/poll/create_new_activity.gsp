<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			
			function setChecked(activityType) {
				$("#activity-list input[checked=checked]").attr('checked', '');
				$("#activity-list ." + activityType).attr('checked', 'checked');
				$("#choose").removeAttr('disabled');
			}
		</script>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
	</head>
	<body>
		<form>
			<h2>Create new activity</h2>
			<ul id="activity-list">
				<li>
					<input type="radio" name="activity" value="announcement" class="announcement" onclick="setChecked('announcement')" /><span>Announcement</span>
					<div>Send an announcement message and organize the responses</div>
				</li>
				<li>
					<input type="radio" name="activity" value="poll" class="poll" onclick="setChecked('poll')" /><span>Poll</span>
					<div>Send a question and analyze the responses</div>
				</li>
			</ul>
		</form>
	</body>
</html>