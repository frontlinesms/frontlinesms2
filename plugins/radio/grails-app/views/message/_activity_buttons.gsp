<%@ page import="frontlinesms2.*" %>
<%@ page import="frontlinesms2.radio.*" %>
<div class="header-buttons">
	<fsms:quickMessage class="section-action-button activity-btn btn"/>
	<g:render template="/wordcloud/wordcloud_actions"/>
	<g:if test="${params.controller!='archive' && !params.inArchive}">
		<g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="archive" id="${ownerInstance?.id}"><g:message code="fmessage.activity.archive" /> <g:message code="${ownerInstance?.shortName.toLowerCase()}.label"/></g:link>
	</g:if>
	<g:elseif test="${!RadioShow.findByOwnedActivity(ownerInstance).get()?.archived}">
		<g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="unarchive" id="${ownerInstance?.id}"><g:message code="fmessage.unarchive"/> <g:message code="${ownerInstance?.shortName.toLowerCase()}.label"/></g:link>
	</g:elseif>
	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
			from="${['export', 'rename', 'delete'] + (ownerInstance.editable?['edit']:[]) + 
			((ownerInstance instanceof Activity)&& (!ownerInstance.archived)?
			['radio.show'+(RadioShow.findByOwnedActivity(ownerInstance).get()?'.assigned':'.unassigned')]:[])}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"
			valueMessagePrefix="${ownerInstance.shortName}.moreactions"/>
</div>
<r:script>
$('.more-actions').bind('change', radioShowAction);
function radioShowAction() {
	if($(".more-actions").val().indexOf("radio.show") != -1)
	{
		$.ajax({
			type:'GET',
			url: url_root + 'radioShow/selectShow',
			data: {
				ownerId:$("#ownerId").val(),
				hideLabel:true },
			success: function(data) {
				launchSmallPopup(i18n("activity.radioShow.assign"), data, i18n("action.ok"));
		}});
	}
}
</r:script>
