<!-- Need to pass model explicitly in nested templates with (grails 1.3.7)-->
<g:set var="model" value="${[contactInstanceTotal:contactInstanceTotal,contactInstanceList:contactInstanceList,
	contactsSection:contactsSection,params:params]}"/>
<f:render template="contact_list" model="${model}"/>
<f:render template="footer" model="${model}"/>
