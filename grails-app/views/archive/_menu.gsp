<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="context-menu main-list" id="archives-menu">
	<li class='section'>
		<ul class='sublist' id="archive-submenu">
				<li class="${(messageSection == 'inbox')? 'selected':''}" >
					<g:link controller="archive" action="inbox" elementId="inbox" onSuccess="loadAllData(data)" params="[viewingArchive: true]">
						<img src='${resource(dir:'images/icons',file:'inboxarchive.png')}' />
						Inbox archive
					</g:link>
				</li>
				<li class="${(messageSection == 'sent')? 'selected':''}" >
					<g:link controller="archive" action="sent" elementId="sent" onSuccess="loadAllData(data)" params="[viewingArchive: true]">
						<img src='${resource(dir:'images/icons',file:'sentarchive.png')}' />
						Sent archive
					</g:link>
				</li>
				<li class="${(messageSection == 'poll' || messageSection == 'announcement') ? 'selected':''}" >
					<g:link controller="archive" action='activityView' elementId="activity" params="[viewingArchive: true]">
						<img src='${resource(dir:'images/icons',file:'activitiesarchive.png')}' />
						Activity archive
					</g:link>
				</li>
				<li class="${(messageSection == 'folder')? 'selected':''}" >
					<g:link controller="archive" action='folderView' elementId="folder" params="[viewingArchive: true]">
						<img src='${resource(dir:'images/icons',file:'foldersarchive.png')}' />
						Folder archive
					</g:link>
				</li>
		</ul>
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
</script>
