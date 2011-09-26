<%@ page import="frontlinesms2.Fconnection" %>
<div id="top">
	<div id="logo"><img src='${resource(dir:'images',file:'logo.png')}' width="36" height="40"/></div>
	<ul id="global-nav">
		<li>
			<g:link class="tab ${params.controller == 'message' ? 'selected' : ''}" url="${[controller:'message']}"	id="tab-messages">Messages ${frontlinesms2.Fmessage.countUnreadMessages()}</g:link>
		</li>
		
		<li>
			<g:link class="tab ${params.controller == 'archive' ? 'selected' : ''}" controller='archive' action="inbox">Archive</g:link>
		</li>

		<li>
			<g:link class="tab ${params.controller == 'contact' ? 'selected' : ''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link>
		</li>
		
		<li>
			<g:link class="tab ${params.controller == 'status' ? 'selected' : ''}" url="${[controller:'status']}" id="tab-status">
				Status
				<img id="indicator" src="${resource(dir:'images/icons',file:'status_red.png')}" />
			</g:link>
		</li>
		
		<li>
			<g:link  class="tab ${params.controller=='search'?'selected':''}" url="${[controller:'search']}" id="tab-search">Search</g:link>
		</li>
	</ul>
</div>
<script>
	$.get(url_root + 'status/trafficLightIndicator', function(data) {
			var imageRoot = "${resource(dir:'images/icons', file:'status_')}";
			$('#indicator').replaceWith("<img id='indicator' src='" + imageRoot + data + ".png'/>");
	});
</script>
