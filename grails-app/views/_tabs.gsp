<div id="top">
	<div id="logo"><img src='${resource(dir:'images',file:'logo.png')}' width="36" height="40"/></div>
	<ul id="global-nav">
		<li>
			<g:link class="tab ${!(params['archived']?.toBoolean()) && ['message','folder','poll'].contains(params.controller)?'selected':''}" url="${[controller:'message']}"	id="tab-messages">Messages ${frontlinesms2.Fmessage.countUnreadMessages()}</g:link>
		</li>
		
		<li>
			<g:link class="tab ${params['archived'] == 'true'? 'selected':''}" controller='message' action="inbox" params="['archived': true]">Archive</g:link>
		</li>

		<li>
			<g:link class="tab ${params.controller=='contact'?'selected':''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link>
		</li>
		
		<li>
			<g:link class="tab ${params.controller=='status'?'selected':''}" url="${[controller:'status']}" id="tab-status">Status</g:link>
		</li>
		
		<li>
			<g:link  class="tab ${params.controller=='search'?'selected':''}" url="${[controller:'search']}" id="tab-search">Search</g:link>
		</li>
	</ul>
</div>
<script>
	$.ajax({
		type:'GET',
		url: url_root + 'status/trafficLightIndicator',
		success: function(data){$('#indicator').attr("src", "../images/status_" + data + ".gif") }
	});
</script>
