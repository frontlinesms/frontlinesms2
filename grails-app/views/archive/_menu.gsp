<%@ page contentType="text/html;charset=UTF-8" %>
<ol class="context-menu" id="archives-menu">
	<li class='section'>
		<ol class='sub-menu' id="archive-submenu">
				<li>
					<g:link class="${(messageSection=='inbox')? 'selected':''}" action="inbox" elementId="inbox" onSuccess="loadAllData(data)">
						Inbox Archive
					</g:link>
				</li>
				<li>
					<g:link class="${(messageSection=='sent')? 'selected':''}" action="sent" elementId="sent" onSuccess="loadAllData(data)">
						Sent Archive
					</g:link>
				</li>
				<li>
					<g:link elementId="poll" class="${(messageSection=='poll')? 'selected':''}" controller="poll" params="${[archive:true]}">
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
