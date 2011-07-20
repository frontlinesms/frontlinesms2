<%@ page contentType="text/html;charset=UTF-8" %>
<ul id="main-tabs">
	<li><g:link
			class="tab ${['message','folder','poll'].contains(params.controller)?'selected':''}" url="${[controller:'message']}"
			id="tab-messages">Messages (${frontlinesms2.Fmessage.countUnreadMessages()})</g:link></li>
	<li><g:link class="tab ${params.controller=='contact'?'selected':''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link></li>
	<li><g:link class="tab ${params.controller=='status'?'selected':''}" url="${[controller:'status']}" id="tab-status"><img id='indicator'/> Status</g:link></li>
	<li><g:link class="tab ${params.controller=='search'?'selected':''}" url="${[controller:'search']}" id="tab-search">Search</g:link></li>
	<li><g:link class="tab ${params.controller=='connection'?'selected':''}" url="${[controller:'settings']}" id="tab-settings">Settings & Plugins</g:link></li>
</ul>
<script>
	$.ajax({
		type:'GET',
		url: '/frontlinesms2/status/trafficLightIndicator',
		success: function(data){$('#indicator').attr("src", "/frontlinesms2/images/" + data + "-status.png") }
	});
</script>