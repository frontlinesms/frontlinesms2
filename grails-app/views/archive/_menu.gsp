<%@ page contentType="text/html;charset=UTF-8" %>
<ol class="context-menu" id="archives-menu">
	<li class='section'>
		<ol class='sub-menu' id="archive-submenu">
				<li class="${(messageSection == 'inbox')? 'selected':''}" >
					<g:link action="inbox" elementId="inbox" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Inbox Archive
					</g:link>
				</li>
				<li class="${(messageSection == 'sent')? 'selected':''}" >
					<g:link action="sent" elementId="sent" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Sent Archive
					</g:link>
				</li>
				<li class="${(messageSection == 'poll')? 'selected':''}" >
					<g:link elementId="poll" controller="poll" params="${[archived: true]}">
						Activity archive
					</g:link>
				</li>
		</ol>
	</li>
</ol>                                                                                                                            

<script>
	$("#archive-menu li a").bind("click", function(event) {
		var source = $(this)
		var allLinks = $("#archive-menu li a")
		allLinks.each(function(index, element) {
			$(element).removeClass("selected")

		});
		source.addClass("selected")
	});
	
	function loadAllData(data) {
		$("#content").html(data)
	}

	$("#inbox").click()
</script>
