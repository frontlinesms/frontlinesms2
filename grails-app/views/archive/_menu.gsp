<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="content-menu" id="archive-menu">
	<li>
		<ol>
				<li>
					<g:remoteLink class="" action="inbox" elementId="inbox" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Inbox Archive
					</g:remoteLink>
				</li>
				<li>
					<g:remoteLink class="" action="sent" elementId="sent" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Sent Archive
					</g:remoteLink>
				</li>
		</ol>
	</li>
 </ul>                                                                                                                            

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