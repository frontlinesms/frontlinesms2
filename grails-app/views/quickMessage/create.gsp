<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>
<div id="tabs">
	<div class="error-panel hide">please enter all the details</div>
	<ul>
		<g:each in="['tabs-1' : 'Enter message', 'tabs-2' : 'Select recipients',
						'tabs-3' : 'Confirm']" var='entry'>
			<g:if test="${configureTabs.contains(entry.key)}">
				<li><a href="#${entry.key}">${entry.value}</a></li>
			</g:if>
		</g:each>
	</ul>

	<g:formRemote name="send-quick-message" url="${[action:'send', controller:'message']}" method="post" onSuccess="addFlashMessage(data)">
		<g:render template="message"/>
		<div id="tabs-2" class="${configureTabs.contains("tabs-2") ? "" : "hide"}">
			<g:render template="select_recipients"/>
		</div>
		<g:render template="confirm"/>
	</g:formRemote>
</div>

<script>
	function initializePopup() {
		$("#tabs-2").contentWidget({
			validate: function() {
				return isGroupChecked("addresses")
			}
		});
	}

	function addFlashMessage(data) {
		$("#header .flash").remove();
		$("#header").prepend("<div class='flash message'>" + data + "</div>");
	}
</script>
