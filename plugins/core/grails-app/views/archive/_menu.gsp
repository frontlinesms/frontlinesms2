<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
	<ul class="context-menu main-list" id="archives-menu">
		<li class='section'>
			<ul class='sublist' id="archive-submenu">
					<li class="${(messageSection == 'inbox')? 'selected':''}" >
						<g:link controller="archive" action="inbox" elementId="inbox" class="archive-section-list" onSuccess="loadAllData(data)">
							Inbox archive
						</g:link>
					</li>
					<li class="${(messageSection == 'sent')? 'selected':''}" >
						<g:link controller="archive" action="sent" elementId="sent" class="archive-section-list" onSuccess="loadAllData(data)">
							Sent archive
						</g:link>
					</li>
					<li class="${(messageSection == 'activity') ? 'selected':''}" >
						<g:link controller="archive" action='activityList' params="[viewingMessages: false]" elementId="activity" class="archive-section-list">
							Activity archive
						</g:link>
					</li>
					<li class="${(messageSection == 'folder')? 'selected':''}" >
						<g:link controller="archive" action='folderList' params="[viewingMessages: false]" elementId="folder" class="archive-section-list">
							Folder archive
						</g:link>
					</li>
			</ul>
		</li>
	</ul>        
</div>                                                                                                                    

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
</script>
