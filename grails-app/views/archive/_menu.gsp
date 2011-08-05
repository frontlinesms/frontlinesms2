<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="content-menu" id="archive-menu">
	<li>
		<ol>
				<li>
					<g:remoteLink action="inbox" id="inbox" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Inbox Archive
					</g:remoteLink>
				</li>
				<li>
					<g:remoteLink action="sent" id="sent" controller="message" params="${[archived: true]}"  onSuccess="loadAllData(data)">
						Sent Archive
					</g:remoteLink>
				</li>
		</ol>
	</li>
 </ul>                                                                                                                            

<script>

	$("#archive-menu li a").bind("click", function() {
	});
	
	function loadAllData(data) {
		$("#content").html(data)
	}
</script>