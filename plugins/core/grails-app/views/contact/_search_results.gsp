<!-- Need to pass model explicitly in nested templates with (grails 1.3.7)-->
<g:set var="model" value="${[contactInstanceTotal:contactInstanceTotal,contactInstanceList:contactInstanceList,
	contactsSection:contactsSection,params:params]}" />
<g:render template="contact_list" model="${model}" plugin="core"/>
<g:render template="footer" model="${model}" plugin="core"/>
