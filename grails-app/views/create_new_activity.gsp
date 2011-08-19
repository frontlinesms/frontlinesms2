<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery"/>
	<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
	<script type="text/javascript">
		url_root = "${request.contextPath}/";
	</script>
	<g:javascript src="application.js"/>
	<g:javascript src="mediumPopup.js"/>
</head>
<body>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">Create new activity or quick message</a></li>
	</ol>

	<div id="tabs-1">
			<ul>
				<li>
					<g:radio value="quickMessage" name="activity"/>
					Quick message
				</li>
				<li>
					<g:radio name="activity" value="poll"/>
					Create new poll
				</li>
				<li>
					<g:radio name="activity" value="subscription"/>
					Manage subscription
				</li>
				<li>
					<g:radio name="activity" value="announcements"/>
					Announcements
				</li>
			</ul>
	</div>
</div>
</body>
</html>

<script>
	function addTabValidations() {
		$("#tabs-1").TabContentWidget({
			validate: function() {
				console.log("validation called");
				var selectedElement = $("input[name='activity']:checked")[0]
				$("#modalBox").dialog("close");
				remoteHash[selectedElement.value].call()
				return false;
			}
		});
	}
</script>