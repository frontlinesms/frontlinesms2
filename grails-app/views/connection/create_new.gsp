<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
<script type="text/javascript">
	url_root = "${request.contextPath}/";
	
	function setChecked(connectionType) {
		$("#type-list input[checked=checked]").attr('checked', '');
		$("#type-list ." + connectionType).attr('checked', 'checked');
		$("#smslib-form").css('display', 'none');
		$("#email-form").css('display', 'none');
		$("#" + connectionType + "-form").css('display', 'inline');
	}
</script>
<g:javascript src="application.js"/>
<g:javascript src="mediumPopup.js"/>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">Choose type</a></li>
		<li><a href="#tabs-2">Enter details</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ol>

	<g:form action="save" id='newConnection'>
		<g:render template="type"/>
		<g:render template="details"/>
		<g:render template="confirm"/>
	</g:form>
</div>