<div id="top">
	<div id="logo"><img src='${resource(dir:'images',file:'logo_radio.png')}' width="36" height="40"/></div>
	<ul id="global-nav">
		<li>
			<g:link class="tab ${params.controller=='message'?'selected':''}" url="${[controller:'message']}" id="tab-messages">Messages <span class="tab-detail">${frontlinesms2.Fmessage.countUnreadMessages()}</span></g:link>
		</li>
		
		<li>
			<g:link class="tab ${params.controller=='archive'?'selected':''}" controller='archive' action="inbox">Archive</g:link>
		</li>

		<li>
			<g:link class="tab ${params.controller=='contact'?'selected':''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link>
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
