<%@ page contentType="text/html;charset=UTF-8" %>
<fsms:menu class="archive">
	<fsms:menuitem class="inbox" selected="${messageSection == 'inbox'}" controller="archive" action="inbox" code="archive.inbox"/>
	<fsms:menuitem class="sent" selected="${messageSection == 'sent'}" controller="archive" action="sent" code="archive.sent"/>
	<fsms:menuitem class="activity" selected="${!inARadioShow && messageSection == 'activity'}" controller="archive" action="activityList" code="archive.activity"/>
	<fsms:menuitem class="folder" selected="${messageSection == 'folder'}" controller="archive" action="folderList" code="archive.folder"/>
	<fsms:menuitem class="radioShow" selected="${inARadioShow}" controller="radioShow" action="showArchive" code="archive.radioshow"/>
</fsms:menu>
