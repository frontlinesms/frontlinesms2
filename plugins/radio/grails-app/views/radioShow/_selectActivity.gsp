<%@ page import="frontlinesms2.radio.RadioShow" %>
<g:form controller="radioShow" action="addActivity">
	<g:hiddenField name="activityId" value="${ownerInstance.id}"/>
	<radio:selectShow from="${radioShows?:RadioShow.findAllByDeleted(false)}" radioShowIntance="${radioShowIntance}" ownerInstance="${activityInstanceToEdit}"/>
</g:form>