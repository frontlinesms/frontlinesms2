<%@ page import="frontlinesms2.radio.RadioShow" %>
<g:form controller="radioShow" action="addActivity">
	<g:hiddenField name="activityId" value="${ownerInstance.id}"/>
	<g:select class="radio-show-select" name='radioShowId' value="${currentShow?.id}"
		    noSelection="${['':currentShow?
		    	g.message(code:'activity.assigned.defaultoption', args:[currentShow.name]):
		    	g.message(code:'activity.unassigned.defaultoption')]}"
		    from='${RadioShow.findAll()}'
		    optionKey="id" optionValue="name"/>
</g:form>