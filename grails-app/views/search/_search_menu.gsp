<%@ page contentType="text/html;charset=UTF-8" %>
		<g:form name="searchDetails" action="search" class="content-menu">
			<div id="search-details" >
				<div id="field">
					<label for="keywords" class="keywords">Keyword or phrase:</label>
					<g:textField name="keywords" id="keywords" value=""/>
				</div>
				<div id="filter">
				  <g:if test="${groupInstanceList || pollInstanceList}">
					  <label>Limit Search to:</label>
					  <div class="field">
						  <select id="group-list" name="groupList">
								  <option value=""></option>
								  <g:each in="${groupInstanceList}" status="i" var="g">
									  <option value="${g.id}">${g.name}</option>
								  </g:each>
						  </select>
					  </div>
					  <div class="field">
						  <select id="poll-list" name="pollList">
								  <option value=""></option>
								  <g:each in="${pollInstanceList}" status="i" var="p">
									  <option value="${p.id}">${p.title}</option>
								  </g:each>
						  </select>
					  </div></g:if>
					
				</div>
			</div>
			<div class="buttons">
				<g:actionSubmit class="search" name="search" value="${message(code: 'default.button.search.label', default: 'Search')}"/>
			</div>
		</g:form>
